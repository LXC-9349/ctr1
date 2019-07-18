package com.ctr.crm.commons.utils;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * 
 * @author Administrator
 *
 */
public class JwtUtil {
	
	private final static Log logger = LogFactory.getLog("exception");
	// 过期时间24小时
	private static final long EXPIRE_TIME = 24 * 3600 * 1000;
    private static final String SECRET = "936be20590cf4014a6456ea92e3467fd";

    public static String generateToken(Integer workerId) {
    	try {
			Date expireTime = new Date(System.currentTimeMillis() + EXPIRE_TIME);
			Algorithm algorithm = Algorithm.HMAC256(SECRET);
			String jwt = JWT.create()
			        .withClaim("workerid", workerId)
			        .withExpiresAt(expireTime)
			        .sign(algorithm);
			return jwt;
		} catch (Exception e) {
			logger.error("jwt generateToken exception. workerid:"+workerId+",reason:" + e.getMessage());
			return null;
		}
    }

    public static boolean verify(String token) {
    	if(token == null) return false;
        try {
        	Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        }catch (Exception e){
        	logger.error("jwt verify exception. token:"+token+",reason:" + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的坐席ID
     */
    public static Integer extractWorkerId(String token) {
    	if(token == null) return null;
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("workerid").asInt();
        } catch (JWTDecodeException e) {
        	logger.error("jwt extractWorkerId exception. token:"+token+",reason:" + e.getMessage());
            return null;
        }
    }
    
    public static void main(String[] args) {
		String token = generateToken(8001);
		System.out.println(token);
		System.out.println(verify(token));
		System.out.println(extractWorkerId(token));
	}
    
}