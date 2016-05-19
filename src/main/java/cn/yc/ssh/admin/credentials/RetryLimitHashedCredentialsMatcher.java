package cn.yc.ssh.admin.credentials;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.management.CacheConfiguration;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.InvalidSessionException;

import cn.yc.ssh.admin.Constants;
import cn.yc.ssh.admin.base.realm.AES;
import cn.yc.ssh.admin.spring.SpringCacheManagerWrapper;

/**
 * 连续输入密码错误锁定帐号
 * @author yc
 *
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    private Cache<String, AtomicInteger> passwordRetryCache;
    
    private Cache<String, AtomicInteger> ipRetryCache;
    
    private CacheManager cacheManager;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    	ipRetryCache = cacheManager.getCache("ipRetryCache");
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
    	String ip = SecurityUtils.getSubject().getSession().getHost();
    	
        String username = (String)token.getPrincipal();
        //同一个用户名多次输入密码错误锁定
        AtomicInteger retryCount = passwordRetryCache.get(username);
        AtomicInteger ipRetryCount = ipRetryCache.get(ip);
        @SuppressWarnings("unchecked")
		Map<String,String> inits = (Map<String, String>) Constants.cache.get("sys_init");
        String pwdCount = inits.get("pwdCount");
        String pwdTime = inits.get("pwdTime");
        if(retryCount == null) {
            retryCount = new AtomicInteger(0);
            SpringCacheManagerWrapper springCacheWrapper = (SpringCacheManagerWrapper) cacheManager;
            CacheConfiguration cacheConfiguration = null;
            org.springframework.cache.Cache springCache = springCacheWrapper.getSpringCache("passwordRetryCache");
            if(springCache.getNativeCache() instanceof Ehcache) {
            	Ehcache ehcache = (Ehcache) springCache.getNativeCache();
            	cacheConfiguration = new CacheConfiguration(ehcache);
            	cacheConfiguration.setTimeToIdleSeconds(60*Integer.parseInt(pwdTime));
            	passwordRetryCache.put(username, retryCount);
            }
        }
        if(retryCount.incrementAndGet() > Integer.parseInt(pwdCount)) {
        	//连续输入密码错误
			throw new ExcessiveAttemptsException();
        }
        if(ipRetryCount == null) {
        	ipRetryCount = new AtomicInteger(0);
        	ipRetryCache.put(ip, ipRetryCount);
        }
        
        boolean matches = false;
		try {
			String password = AES.Decrypt(new String((char[])token.getCredentials()), SecurityUtils.getSubject().getSession().getAttribute("aesKey").toString());
			try {
				SimpleAuthenticationInfo authenticationInfo = (SimpleAuthenticationInfo) info;
				String salt = new String(authenticationInfo.getCredentialsSalt().getBytes());
				password = AES.Encrypt(password,salt.substring(salt.length()-16));
			} catch (Exception e) {
				e.printStackTrace();
			}
			UsernamePasswordToken tokens = new UsernamePasswordToken(token.getPrincipal().toString(), password);
			matches =  super.doCredentialsMatch(tokens, info);
		} catch (InvalidSessionException e) {
			e.printStackTrace();
		} catch (CacheException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(matches) {
		    passwordRetryCache.remove(username);
		    ipRetryCache.remove(ip);
		}
		return matches;
    }
}