package com.accountbook.mvc.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accountbook.config.exception.BaseException;
import com.accountbook.config.http.BaseResponse;
import com.accountbook.config.http.BaseResponseCode;
import com.accountbook.framework.data.domain.MySQLPageRequest;
import com.accountbook.framework.data.domain.PageRequestParameter;
import com.accountbook.mvc.domain.Board;
import com.accountbook.mvc.parameter.BoardParameter;
import com.accountbook.mvc.parameter.BoardSearchParameter;
import com.accountbook.mvc.service.BoardService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/board")
@Api(tags = "게시판API")
public class BoardController {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/list")
	@ApiOperation(value="목록 조회", notes=" 게시물 목록 정보를 조회할 수 있습니다.")	
	public BaseResponse<List<Board>> getList(@ApiParam BoardSearchParameter parameter, @ApiParam MySQLPageRequest pageRequest) {
		logger.info("pageRequest: {}", pageRequest);
		PageRequestParameter<BoardSearchParameter> pageRequestParam = new PageRequestParameter<BoardSearchParameter>(pageRequest, parameter);
		return new BaseResponse<List<Board>>(boardService.getList(pageRequestParam));
	}
	
	@GetMapping("/{boardSeq}")
	@ApiOperation(value="상세 조회", notes=" 게시물 번호에 해당하는 상세정보를 조회할 수 있습니다.")	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "boardSeq", value="게시물 번호", example = "1")
	})
	public BaseResponse<Board> get(@PathVariable int boardSeq) {
		Board board = boardService.get(boardSeq);
		// null 처리
		if(board == null) {
			throw new BaseException(BaseResponseCode.DATA_IS_NULL, new String[] { "게시물"} );
		}
		return new BaseResponse<Board>(boardService.get(boardSeq));
	}
	
	@PutMapping
	@ApiOperation(value="등록/수정 처리", notes=" 신규 게시물 저장 및 기존 게시물 업데이트가 가능합니다.")	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "title", value="제목", example = "spring"),
		@ApiImplicitParam(name = "contents", value="내용", example = "spring 강좌"),
	})
	public BaseResponse<Integer> save(BoardParameter param) {
		int boardSeq = param.getBoardSeq();
		// 제목 필수 체크
		if(StringUtils.isEmpty(param.getTitle()) ) {
			throw new BaseException(BaseResponseCode.VALIDATE_REQUIRED, new String[] { "title", "제목"} );
		}
		// 내용 필수 체크
		if(StringUtils.isEmpty(param.getContents()) ) {
			throw new BaseException(BaseResponseCode.VALIDATE_REQUIRED, new String[] { "contents", "내용" } );
		}
		// boardSeq가 있는 경우 체크
		if(boardSeq != 0) {
			Board board =boardService.get(boardSeq);
			if(board == null) {
				throw new BaseException(BaseResponseCode.DATA_IS_NULL, new String[] { "boardSeq"} );
			}
		}
		boardService.save(param);
		return new BaseResponse<Integer>(param.getBoardSeq()); 
	}
	
	@DeleteMapping("/{boardSeq}")
	@ApiOperation(value="삭제 처리", notes=" 게시물 번호에 해당하는 정보를 삭제합니다.")	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "boardSeq", value="게시물 번호", example = "1"),
	})
	public BaseResponse<Boolean> delete(@PathVariable int boardSeq) {
		Board board = boardService.get(boardSeq);
		if(board == null) {
			return new BaseResponse<Boolean>(false); 
		} 
		boardService.delete(boardSeq);
		return new BaseResponse<Boolean>(true);
	}
	
	@PutMapping("/saveList1")
	@ApiOperation(value="대용량 등록처리1", notes="대용량 등록처리1")	
	public BaseResponse<Boolean> saveList1() {
		int count = 0;
		// 테스트를 위한 랜덤 1000건의 데이터를 생성
		List<BoardParameter> list = new ArrayList<BoardParameter>();
		while(true) {
			count++;
			String title = RandomStringUtils.randomAlphabetic(10);
			String contents = RandomStringUtils.randomAlphabetic(10);
			list.add(new BoardParameter(title, contents));
			if(count >= 10000) {
				break;
			}
		}
		long start = System.currentTimeMillis();
		boardService.saveList1(list);
		long end = System.currentTimeMillis();
		logger.info("실행시간 : {}", (end - start) / 1000.0);
		
		return new BaseResponse<Boolean>(true);
	}
	
	@PutMapping("/saveList2")
	@ApiOperation(value="대용량 등록처리2", notes="대용량 등록처리2")		
	public BaseResponse<Boolean> saveList2() {
		int count = 0;
		// 테스트를 위한 랜덤 1000건의 데이터를 생성
		List<BoardParameter> list = new ArrayList<BoardParameter>();
		while(true) {
			count++;
			String title = RandomStringUtils.randomAlphabetic(10);
			String contents = RandomStringUtils.randomAlphabetic(10);
			list.add(new BoardParameter(title, contents));
			if(count >= 10000) {
				break;
			}
		}
		long start = System.currentTimeMillis();
		boardService.saveList2(list);
		long end = System.currentTimeMillis();
		logger.info("실행시간 : {}", (end - start) / 1000.0);
		
		return new BaseResponse<Boolean>(true);
	}
}
