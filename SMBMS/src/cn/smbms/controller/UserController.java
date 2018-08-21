package cn.smbms.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController{
	private Logger logger=Logger.getLogger(UserController.class);
	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;
	/**
	 * 跳转登录页面。。。。。9
	 * @return
	 */
	@RequestMapping(value="/login.html")
	public String login(){
		logger.debug("UserController welcome SMBMS==============================");
		return "login";
	}
	
	/**
	 * 实现登录方法
	 * 登录成功跳转系统首页，否则跳转登录页
	 * @return
	 */
	@RequestMapping(value="/dologin.html",method=RequestMethod.POST)
	public String doLogin(@RequestParam String userCode,
						  @RequestParam String userPassword,
						  HttpSession session,
						  HttpServletRequest request) throws Exception{
		logger.debug("doLogin================================");
		//调用service方法，对用户进行匹配
		User user=userService.login(userCode, userPassword);
		/**
		 * 对用户登录进行判断	
		 */
		if(user!=null){
			//放入session
			session.setAttribute(Constants.USER_SESSION,user);
			//用户不为空，则为登录成功,页面跳转frame.jsp
			return "redirect:/user/main.html";
		}else{
			request.setAttribute("error","用户名或密码错误");
			//页面跳转login.jsp
			return "login";
		}
	}
	
	@RequestMapping(value="/main.html")
	public String main(HttpSession session){
		if(session.getAttribute(Constants.USER_SESSION)==null){
			return "redirect:/user/login.html";
		}
		return "frame";
	}
	
	/**
	 * 分页、模糊、根据userRole查询
	 * @param model
	 * @param queryUserName
	 * @param queryUserRole
	 * @param pageIndex
	 * @return
	 */
	@RequestMapping(value="/userlist.html")
	public String getUserList(Model model,
							  @RequestParam(value="queryname",required=false)String queryUserName,
							  @RequestParam(value="queryUserRole",required=false)String queryUserRole,
							  @RequestParam(value="pageIndex",required=false)String pageIndex){
		logger.info("getUserList------querUserNameString:"+queryUserName);
		logger.info("getUserList------querUserRole:"+queryUserRole);
		logger.info("getUserList------pageIndex:"+pageIndex);
		int _queryUserRole=0;
		List<User> userList=null;
		//设置页面容量
		int pageSize=Constants.pageSize;
		//当前页码
		int currentPageNo=1;
		if(queryUserName==null){
			queryUserName="";
		}
		if(queryUserRole!=null&&!queryUserRole.equals("")){
			_queryUserRole=Integer.parseInt(queryUserRole);
		}
		if(pageIndex!=null){
			try{
				currentPageNo=Integer.valueOf(pageIndex);
			}catch (NumberFormatException e) {
				return "redirect:/user/syserror.html";
			}
		}
		
		//总数量（表）
		int totalCount=userService.getUserCount(queryUserName, _queryUserRole);
		//总页数
		PageSupport pages=new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);
		int totalPageCount=pages.getTotalPageCount();
		
		//控制首页和尾页
		if(currentPageNo<1){
			currentPageNo=1;
		}else if(currentPageNo>totalPageCount){
			currentPageNo=totalPageCount;
		}
		userList=userService.getUserList(queryUserName, _queryUserRole,
										currentPageNo, pageSize);
		model.addAttribute("userList",userList);
		List<Role> roleList=null;
		roleList=roleService.getRoleList();
		model.addAttribute("roleList",roleList);
		model.addAttribute("queryUserName",queryUserName);
		model.addAttribute("queryUserRole",queryUserRole);
		model.addAttribute("totalPageCount",totalPageCount);
		model.addAttribute("totalCount",totalCount);
		model.addAttribute("currentPageNo",currentPageNo);
		return "userlist";
	}
	
	@RequestMapping(value="/syserror.html")
	public String sysError(){
		return "syserror";
	}
	
	/**
	 * 跳转到新增页面
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/useradd.html",method=RequestMethod.GET)
	public String addUser(@ModelAttribute("user")User user){
		return "useradd";
	}
	/*@RequestMapping(value="/add.html",method=RequestMethod.GET)
	public String add(@ModelAttribute("user")User user){
		return "user/useradd";
	}*/
	/**
	 * 保存新增信息
	 */
	/*@RequestMapping(value="/addsave.html",method=RequestMethod.POST)
	public String addUserSave(User user,HttpSession session){
		user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
		user.setCreationDate(new Date());
		if(userService.add(user)){
			return "redirect:/user/userlist.html";
		}
		return "useradd";
	}*/
	/*@RequestMapping(value="/add.html",method=RequestMethod.POST)
	public String addSave(@Valid User user,BindingResult bindingResult,HttpSession session){
		if(bindingResult.hasErrors()){
			logger.debug("add user vaildated has error=============");
			return "user/useradd";
		}
		user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
		user.setCreationDate(new Date());
		if(userService.add(user)){
			return "redirect:/user/userlist.html";
		}
		return "user/useradd";
	}*/
	
	/**
	 * 多文件上传
	 * @param user
	 * @param session
	 * @param request
	 * @param attach
	 * @return
	 */
	@RequestMapping(value="/addsave.html",method=RequestMethod.POST)
	public String addUserSave(User user,
							  HttpSession session,
						  HttpServletRequest request,
							  @RequestParam(value="attachs",required=false)
							  MultipartFile[] attachs){
		String idPicPath=null;
		String workPicPath=null;
		String errorInfo=null;
		boolean flag=true;
		//判断文件是否为空
		String path=request.getSession().getServletContext()
				.getRealPath("statics"+File.separator+"uploadfiles");
		logger.debug("uploadFile path=========="+path);
		for(int i=0;i<attachs.length;i++){
			MultipartFile attach=attachs[i];
			if(!attach.isEmpty()){
				if(i==0){
					errorInfo="uploadFileError";
				}else if(i==1){
					errorInfo="uploadWpError";
				}
				//原文件
				String oldFileName=attach.getOriginalFilename();
				//原文件后缀名
				String prefix=FilenameUtils.getExtension(oldFileName);
				//设置文件最大值 ====》上传文件不得超过500kb
				int filesize=500000;
				//判断上传文件是否大于文件最大值
				if(attach.getSize()>filesize){
					//上传文件超过500kb
					request.setAttribute(errorInfo,"*上传文件大小不得超过500KB");
					flag=false;
				}else if(prefix.equalsIgnoreCase("png")
						||prefix.equalsIgnoreCase("jpg")
						||prefix.equalsIgnoreCase("jpeg")
						||prefix.equalsIgnoreCase("pneg")){
					//上传文件格式不正确
					String fileName=System.currentTimeMillis()+RandomUtils.nextInt(1000000)+"_Person.jpg";
					logger.debug("new fileName=========="+attach.getName());
					File targetFile=new File(path,fileName);
					if(!targetFile.exists()){
						targetFile.mkdirs();
					}
					//保存
					try {
						attach.transferTo(targetFile);
					} catch (Exception e) {
						e.printStackTrace();
						request.setAttribute(errorInfo,"*上传失败!");
						flag=false;
					}
					if(i==0){
						idPicPath=path+File.separator+fileName;
					}else if(i==1){
						workPicPath=path+File.separator+fileName;
					}
					logger.debug("idPicPath============"+idPicPath);
					logger.debug("workPicPath============"+workPicPath);
			}else{
				request.setAttribute(errorInfo,"*上传图片格式不正确");
				flag=false;
			}
		}
		}
		if(flag){
			user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
			user.setCreationDate(new Date());
			user.setIdPicPath(idPicPath);
			user.setWorkPicPath(workPicPath);
			if(userService.add(user)){
				return "redirect:/user/userlist.html";
			}
		}
		return "useradd";
	}
	
	/**
	 * 修改
	 * @param uid
	 * @param model
	 * @return
	 */
	public String getUserById(@RequestParam String uid,Model model){
		logger.debug("getUserById uid==================="+uid);
		return "usermodify";
	}
	
	@RequestMapping(value="/ucexist.html")
	@ResponseBody
	public Object userCodeIsExit(@RequestParam String userCode){
		logger.debug("userCodeIsExit===============:"+userCode);
		HashMap<String,String> resultMap=new HashMap<String,String>();
		if(StringUtils.isNullOrEmpty(userCode)){
			resultMap.put("userCode","exist");
		}else{
			User user=userService.selectUserCodeExist(userCode);
			if(null!=user){
				resultMap.put("userCode","exist");
			}else{
				resultMap.put("userCode","noexist");
			}
		}
		return JSONArray.toJSONString(resultMap);
	}
	
	@RequestMapping(value="/view",method=RequestMethod.GET)
	@ResponseBody
	public User view(@RequestParam String id){
		logger.debug("view=======id:"+id);
		User user=new User();
		try {
			user=userService.getUserById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	
}
