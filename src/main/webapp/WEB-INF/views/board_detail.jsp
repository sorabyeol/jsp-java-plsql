<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세보기</title>
<style>
    table { width: 600px; border-collapse: collapse; }
    th, td { border: 1px solid #ccc; padding: 10px; }
    th { background: #eee; width: 100px; }
    .content { height: 200px; vertical-align: top; }
</style>
</head>
<body>
    <h2>게시글 상세</h2>
    <table>
        <tr><th>제목</th><td>${board.TITLE}</td></tr>
        <tr><th>작성자</th><td>${board.WRITER}</td></tr>
        <tr><th>작성일</th><td>${board.CREATED_DATE}</td></tr>
        <tr><th>조회수</th><td>${board.VIEW_COUNT}</td></tr>
        <tr><th>내용</th><td class="content">${board.CONTENT}</td></tr>
    </table>
    <br>
    
	<div class="file-list-area">
	    <h4>첨부파일</h4>
	    <c:choose>
	        <c:when test="${not empty fileList}">
	            <ul>
	                <c:forEach var="file" items="${fileList}">
						<li>
						    <span style="font-weight:bold;">📄 ${file.ORIGINAL_NAME}</span> 
						    <span style="color:gray;">(${file.FILE_SIZE} bytes)</span>
						    
						    <a href="download.do?fileId=${file.FILE_ID}" class="btn-download">
						        <button type="button">다운로드</button>
						    </a>
						</li>
	                </c:forEach>
	            </ul>
	        </c:when>
	        <c:otherwise>
	            <p>첨부된 파일이 없습니다.</p>
	        </c:otherwise>
	    </c:choose>
	</div>
	
    <hr>
    <a href="boardList.do">목록으로</a>
</body>
</html>