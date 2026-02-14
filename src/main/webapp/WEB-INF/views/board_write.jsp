<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 등록</title>
<style>
    /* 전체 레이아웃을 상세 화면과 동일하게 왼쪽 정렬 (너비 600px 고정) */
    body { font-family: 'Malgun Gothic', sans-serif; font-size: 15px; color: #333; margin: 20px; width: 600px; }
    
    /* 제목 스타일 (상세 화면과 통일) */
    h3 { font-size: 22px; color: #444; margin-bottom: 20px; }
    
    /* 1. 테이블 디자인 (상세 화면과 동일하게 600px 너비) */
    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    th, td { border: 1px solid #ccc; padding: 10px; font-size: 14px; }
    th { background-color: #eee; width: 100px; text-align: center; font-weight: bold; }
    
    /* 입력창 스타일 */
    input[type="text"], textarea { 
        width: 100%; 
        padding: 8px; 
        font-size: 14px; 
        border: 1px solid #ddd; 
        border-radius: 4px; 
        box-sizing: border-box; 
        font-family: inherit;
    }
    textarea { resize: vertical; min-height: 200px; line-height: 1.5; }
    
    /* 2. 하단 버튼 영역 (상세 화면과 동일하게 우측 정렬) */
    .btn-container {
        display: flex;
        justify-content: flex-end; /* 우측 정렬 */
        gap: 8px; /* 버튼 사이 간격 */
        margin-top: 20px;
    }

    /* 공통 버튼 스타일 (상세 화면 버튼 스타일 적용) */
    .btn {
        padding: 8px 18px;
        font-size: 14px;
        font-weight: bold;
        border-radius: 4px;
        border: 1px solid #ccc;
        cursor: pointer;
        transition: 0.2s;
        text-decoration: none;
        display: inline-block;
    }

    /* 등록 버튼 (상세 화면의 긍정적인 느낌을 위해 녹색 계열 유지) */
    .btn-save { background-color: #4CAF50; color: white; border: 1px solid #45a049; }
    .btn-save:hover { background-color: #45a049; }

    /* 목록 버튼 (상세 화면과 동일한 회색 계열) */
    .btn-list { background-color: #f7f7f7; color: #333; }
    .btn-list:hover { background-color: #e9e9e9; }
</style>
</head>
<body>

    <h3>게시글 등록</h3>
    
    <form action="${pageContext.request.contextPath}/board/insertBoard.do" method="post" enctype="multipart/form-data">
        <table>
            <tr>
                <th>제목</th>
                <td><input type="text" name="TITLE" placeholder="제목을 입력하세요" required></td>
            </tr>
            <tr>
                <th>작성자</th>
                <td><input type="text" name="WRITER" placeholder="작성자 이름" required></td>
            </tr>
            <tr>
                <th>내용</th>
                <td><textarea name="CONTENT" placeholder="내용을 입력하세요"></textarea></td>
            </tr>
            <tr>
                <th>첨부파일</th>
                <td><input type="file" name="uploadFile" multiple="multiple"></td>
            </tr>
        </table>
        
        <div class="btn-container">
            <a href="${pageContext.request.contextPath}/board/boardList.do" class="btn btn-list">목록으로</a>
            <button type="submit" class="btn btn-save">등록하기</button>
        </div>
    </form>

</body>
</html>