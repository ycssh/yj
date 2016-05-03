package cn.yc.ssh.admin.base.web.jcaptcha;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import cn.yc.ssh.admin.Constants;

/**
 * 验证码过滤器
 */
public class JCaptchaValidateFilter extends AccessControlFilter {

    private boolean jcaptchaEbabled = true;//是否开启验证码支持

    private String jcaptchaParam = "jcaptchaCode";//前台提交的验证码参数名

    private String failureKeyAttribute = "shiroLoginFailure"; //验证码验证失败后存储到的属性名
    
    private Cache<String, AtomicInteger> ipRetryCache;
    
    public JCaptchaValidateFilter(CacheManager cacheManager) {
    	ipRetryCache = cacheManager.getCache("ipRetryCache");
    }

    public void setJcaptchaEbabled(boolean jcaptchaEbabled) {
        this.jcaptchaEbabled = jcaptchaEbabled;
    }
    public void setJcaptchaParam(String jcaptchaParam) {
        this.jcaptchaParam = jcaptchaParam;
    }
    public void setFailureKeyAttribute(String failureKeyAttribute) {
        this.failureKeyAttribute = failureKeyAttribute;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        //1、设置验证码是否开启属性，页面可以根据该属性来决定是否显示验证码
    	
    	String ip = SecurityUtils.getSubject().getSession().getHost();
    	 AtomicInteger retryCount = ipRetryCache.get(ip);
         Map<String,String> inits = (Map<String, String>) Constants.cache.get("sys_init");
         String pwdCount = inits.get("pwdCount");
         if(retryCount!=null&&retryCount.intValue()>=Integer.valueOf(pwdCount)) {
        	 setJcaptchaEbabled(true);
         }else{
        	 setJcaptchaEbabled(false);
         }
        request.setAttribute("jcaptchaEbabled", jcaptchaEbabled);

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        //2、判断验证码是否禁用 或不是表单提交（允许访问）
        if (jcaptchaEbabled == false || !"post".equalsIgnoreCase(httpServletRequest.getMethod())||retryCount.intValue()==Integer.valueOf(pwdCount)) {
            return true;
        }
        //3、此时是表单提交，验证验证码是否正确
        return JCaptcha.validateResponse(httpServletRequest, httpServletRequest.getParameter(jcaptchaParam));
    }
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //如果验证码失败了，存储失败key属性
        request.setAttribute(failureKeyAttribute, "jCaptcha.error");
        return true;
    }
}
