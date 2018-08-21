package cn.smbms.controller;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.smbms.pojo.User;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;

@Controller
public class LoginContorller {
	
	private Logger logger=Logger.getLogger(LoginContorller.class);
	@Resource
	public UserService userService;
	
	@RequestMapping(value="login.html")
	public String Login(){
		return "login";
	}
	
	@RequestMapping(value="/doLogin.html",method=RequestMethod.POST)
	public String doLogin(@RequestParam String userCode,
						@RequestParam String userPassword,
						HttpServletRequest request,
						HttpSession session) throws Exception{
		User user=userService.login(userCode, userPassword);
		if(null!=user){
			session.setAttribute(Constants.USER_SESSION,user);
			return "redirect:/sys/main.html";
		}else{
			request.setAttribute("error","用户名或者密码不正确");
			return "login";
		}
	}
	
	/**
	 * 注销
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/logout.html")
	public String logout(HttpSession session){
		session.removeAttribute(Constants.USER_SESSION);
		return "login";
	}
	
	@RequestMapping(value="/sys/main.html")
	public String main(){
		return "frame";
	}
}
