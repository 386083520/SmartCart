package org.lmw.tools.util;
public class OrderUrlUtil {
	/**
	 * 用户注册URL
	 */
	//public static final String REGISTER_URL = "userSetvlet/userRegister?";
	public static final String REGISTER_URL = "GetMobileRegisterServlet?";
	/**
	 * 用户登陆URL
	 */
	//public static final String LOGIN_URL = "userSetvlet/userLogin?";
	public static final String LOGIN_URL = "GetMobileLoginClServlet?";
	
	/**
	 * 传输用户账单信息
	 * */
	//public static final String GOODSLIST_URL = "GetMobileGoodsListServlet?";
	public static final String GOODSLIST_URL = "GetMobilePayServlet?";
	/**
	 * 通过码数字获取信息URL
	 */
	//public static final String CODE_URL = "goodsInfoSetvlet/goodsInfo?";
	public static final String CODE_URL = "GetMobileGoodsInfoServlet?";
	/**
	 * 获取账单URL
	 */
	//public static final String CODE_URL = "goodsInfoSetvlet/goodsInfo?";
	public static final String PAYLIST_URL = "GetMobileQueryBillServlet?";
	/**
	 * 支付URL
	 */
	public static final String PAY_URL = "GetMobilePayPasswdServlet?";
	/**
	 * 用户修改基本信息
	 */
	public static final String MODIFY_BASE_INFOR = "userInforManage/baseModify?";
	
	/**
	 * 用户修改密码
	 */
	public static final String MODIFY_PASSWORD_INFOR = "userInforManage/passwordModify?";
	
	
}

