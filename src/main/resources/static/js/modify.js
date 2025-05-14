// add.png 버튼 클릭시
$(".addFileButton").on("click", function(evt){
    let inputFileTag = `<tr>
                            <td colspan="2"><input type="file" class="form-control" name="modifyNewFile" onchange="showPreview(this);"></td>
                            <td><input type="button" class="btn btn-info" value="파일첨부취소" onclick="cancelAddFile(this);"></td>
                        </tr>`;

    let obj = $(evt.target);
    // console.log(obj);
    let row = $(obj).parent().parent();

    $(inputFileTag).insertBefore(row);
});

function showPreview(obj){
    console.log(obj.files);
    if(obj.files[0].size > 1 * 1024 * 1024){
        alert("1MB 이하의 파일만 업로드 할 수 있습니다.")
        obj.value = "";
        return;
    }

    // 파일 타입 확인
    let imageType = ["image/jpeg", "image/png", "image/gif", "image/jpg"];
    let fileType = obj.files[0].type;
    let fileName = obj.files[0].name;

    if (imageType.indexOf(fileType) != -1){ // 이미지 파일이다
        let reader = new FileReader(); // FileReader 객체 생성
        reader.readAsDataURL(obj.files[0]); // 업로드된 파일을 읽어온다.
        reader.onload = function(e){ // reader 객체에 의해 파일 읽기를 완료하면 실행되는 콜백함수
            console.log(e.target);
            let imgTag = `<div style="padding : 6px">
                            <img src="${e.target.result}" width="50px">
                            <span>${fileName}</span></div>`;
            $(imgTag).insertAfter(obj);

        }
    } else {
        // 이미지 파일이 아닌 경우
        let imgTag = `<div style="padding : 6px">
                                    <img src="/images/noimage.png" width="50px">
                                    <span>${fileName}</span></div>`;
        $(imgTag).insertAfter(obj);
    }
}

function cancelAddFile(obj){
    // [파일첨부취소] 버튼을 클릭하면, 추가한 새 업로드 파일 미리보기 삭제
    console.log(obj);
    let fileTag = $(obj).parent().prev().children().eq(0); // input type=file 태그
    $(fileTag).val(""); // 선택파일 초기화
    $(fileTag).parent().parent().remove(); // tr삭제

}

$(".fileCheckbox").on("change", function(){
    updateSelectedFileStatus();
});

function updateSelectedFileStatus(){
    let chkCount = $(".fileCheckbox:checked").length;

    if (chkCount > 0){
        $(".removeUpFileBtn").removeAttr("disabled").val(chkCount + "개 파일 삭제")
    } else {
        $(".removeUpFileBtn").attr("disabled", true).val("선택된 파일이 없습니다");
    }

    // selectAll 체크박스 상태 업데이트
    let total = $(".fileCheckbox").length;
    let checked = $(".fileCheckbox:checked").length;

    $("#selectAll").prop("checked", total > 0 && total === checked);
}

$("#selectAll").on("change", function(){
    let isChecked = $(this).is(":checked");
    $(".fileCheckbox").prop("checked", isChecked);
    updateSelectedFileStatus();
});

$(".removeUpFileBtn").on("click", function(){
    let removeFileArr = [];

    $(".fileCheckbox").each(function(i, item){
        if($(item).is(":checked")){
            let tmp = $(item).data("file-id");
            // console.log("삭제할 파일의 file-id", tmp);
            removeFileArr.push(tmp);
        }
    });

    console.log("삭제할 파일 : ", removeFileArr);

    $.each(removeFileArr, function(i, item){
        $.ajax({
            url: "/board/modifyRemoveFileCheck", // 데이터가 송수신될 서버의 주소
            type: "POST", // 통신 방식 (GET, POST, PUT, DELETE)
            dataType: "json", // 수신받을 데이터의 타입 (MIME TYPE)
            data: {"removeFileNo" : item },
            // async: false, // 비동기옵션 off
            success: function(data){ // 통신이 성공하면 수행할 함수
                console.log(data);
                if(data.msg == "success"){
                    $("input.fileCheckbox[data-file-id='" + item +"']").closest("tr").css("opacity", 0.2);
                }
            },
            error: function(){
    
            },
            complete: function(){
            }
        });
    });
});

$(".cancelRemove").on("click", function(){
    $.ajax({
        url: "/board/cancelRemFiles", // 데이터가 송수신될 서버의 주소
        type: "POST", // 통신 방식 (GET, POST, PUT, DELETE)
        dataType: "json", // 수신받을 데이터의 타입 (MIME TYPE)
        // async: false, // 비동기옵션 off
        success: function(data){ // 통신이 성공하면 수행할 함수
            console.log(data);
            if(data.msg == "success"){
                $(".fileCheckbox").each(function(i, item){
                    $(item).prop("checked", false); // 체크해제
                    $(item).closest("tr").css("opacity", 1);
                });

                // $(".removeUpFileBtn").attr("disabled", true);
                // $(".removeUpFileBtn").val("선택된 파일이 없습니다");
                updateSelectedFileStatus();
            }
        },
        error: function(){

        },
        complete: function(){
        }
    });
})