package org.cisiondata.modules.identity.controller;

import org.cisiondata.modules.abstr.web.ResultCode;
import org.cisiondata.modules.abstr.web.WebResult;
import org.cisiondata.modules.identity.service.IIdCardService;
import org.cisiondata.utils.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/")
public class IdCardController {
	
	@Autowired
	private IIdCardService idCardService = null;
	@ResponseBody
	@RequestMapping(value="/educations")
	public WebResult readCardDatas(String name, String university, String department, String major,String beginTime){
		WebResult result = new WebResult();
		try {
			result.setData(idCardService.readESCard(name, university, department, major, beginTime));
			result.setCode(ResultCode.SUCCESS.getCode());
		}catch(BusinessException bu){
			result.setCode(bu.getCode());
			result.setFailure(bu.getDefaultMessage());
		} catch (Exception e) {
			result.setCode(ResultCode.FAILURE.getCode());
			result.setFailure(e.getMessage());
		}
		return result;
	}
	@RequestMapping(value="/card",method = RequestMethod.GET)
	public ModelAndView toMoblie(){
		return new ModelAndView("/user/user_idCard");
	}
}
