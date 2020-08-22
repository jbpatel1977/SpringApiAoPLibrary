package com.jp.genesis.audit.aspect;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.jp.genesis.model.Employee;



@Aspect
@Component
public class GenesisApiAuditAspect {
	public static final Logger LOG = LoggerFactory.getLogger(GenesisApiAuditAspect.class);

	@Before("@annotation(GenesisApiAudit)")
	public void logApiRequest(JoinPoint jp) {
		String methodName = jp.getSignature().getName();  		

		LOG.info("@@@@ jp.getKind() : " + jp.getKind()); //method-execution
		LOG.info("@@@@ jp.getSignature(): " + jp.getSignature()); //ResponseEntity com.jp.genesis.controller.EmployeeRestController.getEmployee(int)
		LOG.info("@@@@ jp.getSourceLocation(): " + jp.getSourceLocation()); //org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint$SourceLocationImpl@b203da53
		LOG.info("@@@@ jp.getTarget(): " + jp.getTarget()); //com.jp.genesis.controller.EmployeeRestController@e3a1b9f
		LOG.info("@@@@ jp.getThis().toString(): " + jp.getThis().toString()); //com.jp.genesis.controller.EmployeeRestController@e3a1b9f

		Object[] argList = jp.getArgs();
		if(null != argList) {
			for (Object object : argList) {
				LOG.info("@@@@ ... jp.getArgs(): " + object.toString());
			}
		}


		LOG.info("@@@@ Executing method 1 : " + methodName); // getEmployee
		LOG.info("@@@@ jp.toLongString() 1 : " + jp.toLongString()); // execution(public org.springframework.http.ResponseEntity com.jp.genesis.controller.EmployeeRestController.getEmployee(int))
		LOG.info("@@@@ jp.toShortString() 1 : " + jp.toShortString()); //execution(EmployeeRestController.getEmployee(..))
		LOG.info("@@@@ jp.toString() 1 : " + jp.toString());  // execution(ResponseEntity com.jp.genesis.controller.EmployeeRestController.getEmployee(int))     

	}

	@Before("@annotation(GenesisApiAuditLight)")
	public void logApiRequestLight(JoinPoint jp) {
		String methodName = jp.getSignature().getName();  		

		LOG.info("@@@@---- jp.getSignature(): " + jp.getSignature()); //ResponseEntity com.jp.genesis.controller.EmployeeRestController.getEmployee(int)
		LOG.info("@@@@---- Executing method 1 : " + methodName); // getEmployee
		LOG.info("@@@@---- jp.toString() 1 : " + jp.toString());  // execution(ResponseEntity com.jp.genesis.controller.EmployeeRestController.getEmployee(int))     

		try {
			Object[] argList = jp.getArgs();
			if(null != argList) {
				for (Object object : argList) {
					LOG.info("@@@@ ... argument : " + object.toString());
				}
			}
		}
		catch (Exception ex ) {
			LOG.error("@@@@ ... Exception in : " + jp.getSignature());
		}

	}


	@AfterReturning(pointcut="@annotation(GenesisApiAuditLight)", returning="returnObject")	
	public void logApiResponseLight(Object returnObject) {	
		if (null != returnObject) {
			if (returnObject instanceof ResponseEntity ){
//				ResponseEntity<?> responseEntity = (ResponseEntity<?>)returnObject;
				ResponseEntity<?> responseEntity = (ResponseEntity<?>)returnObject;
				LOG.info("*** responseEntity.getStatusCode() : " + responseEntity.getStatusCode()); // 200 OK
				LOG.info("*** responseEntity.getStatusCodeValue() : " + responseEntity.getStatusCodeValue()); // 200

				if (responseEntity.getBody() instanceof Optional<?>) {
					LOG.info("@@@@ *** responseEntity.getBody() : " + ((Optional<Employee>)responseEntity.getBody()).get().getFirst_name()); //Optional[com.jp.genesis.model.Employee@719afb22]
					LOG.info("@@@@ *** (Optional<Employee>) responseEntity.getBody() : " + ((Optional<Employee>)responseEntity.getBody()).get().toString()); 
				}
				else if (responseEntity.getBody() instanceof String) {
					LOG.info("@@@@ *** responseEntity.getBody() : " + (responseEntity.getBody()).toString()); 
				}
				LOG.info("@@@@ *** responseEntity.getBody() : " + (responseEntity.getBody()).toString()); 
			}
			else {
				LOG.info("@@@@ *** not instance of ResponseEntity **** ");
			}
		}
		else {
			LOG.info("@@@@ *** if (null == returnObject)  **** ");
		}
		LOG.info("@@@@ ---- returnObject.toString()  : " + returnObject.toString());	//<200 OK OK,Optional[com.jp.genesis.model.Employee@35b2478],[]>
		LOG.info("@@@@ ---- returnObject.getClass().getCanonicalName(): " + returnObject.getClass().getCanonicalName()); // org.springframework.http.ResponseEntity
	}

	//	  @AfterThrowing("restControllerApplicationPackagePointCut() || restControllerPointCut()")
	//	  public void logApiError() {
	//			LOG.info("**** API error case  : ");		
	//	  }


}
