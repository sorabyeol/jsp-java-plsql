<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<title>게시글 수정</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; margin: 20px; width: 800px; }
    table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
    th, td { border: 1px solid #ccc; padding: 10px; }
    th { background-color: #f4f4f4; width: 150px; }
    input[type="text"], textarea { width: 100%; box-sizing: border-box; padding: 5px; }
    textarea { height: 200px; }
    .btn-container { text-align: right; }
    .btn { padding: 8px 15px; cursor: pointer; border: 1px solid #ccc; background: #fff; }
    .btn-save { background-color: #2196F3; color: white; border: none; }
</style>
</head>
<body>

    <h2>게시글 수정</h2>
    
		<form action="${pageContext.request.contextPath}/board/updateBoardAction.do" method="post" enctype="multipart/form-data">        <input type="hidden" name="BOARD_ID" value="${board.BOARD_ID}">
        
        <table>
            <tr>
                <th>제목</th>
                <td><input type="text" name="TITLE" value="${board.TITLE}" required></td>
            </tr>
            <tr>
                <th>작성자</th>
                <td><input type="text" value="${board.WRITER}" disabled></td>
            </tr>
            <tr>
                <th>내용</th>
                <td><textarea name="CONTENT" required>${board.CONTENT}</textarea></td>
            </tr>
            
			<tr>
			    <th>기존 파일</th>
			    <td>
			        <c:forEach var="file" items="${fileList}">
			            <div style="margin-bottom: 5px;">
			                📎 ${file.ORIGINAL_NAME}
			                <label style="color: red; margin-left: 10px;">
			                    <input type="checkbox" name="delFiles" value="${file.FILE_ID}"> 삭제
			                </label>
			            </div>
			        </c:forEach>
			    </td>
			</tr>
            
            <tr>
                <th>파일 추가</th>
                <td><input type="file" name="uploadFile" multiple="multiple"></td>
            </tr>
        </table>
        
        <div class="btn-container">
            <button type="submit" class="btn btn-save">수정완료</button>
            <button type="button" class="btn" onclick="history.back();">취소</button>
        </div>
    </form>

</body>
</html>