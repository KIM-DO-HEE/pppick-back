package com.pickx3.controller;

import com.pickx3.domain.entity.work_package.Work;
import com.pickx3.domain.entity.work_package.dto.work.WorkForm;
import com.pickx3.domain.entity.work_package.dto.work.WorkResponseDTO;
import com.pickx3.domain.entity.work_package.dto.work.WorkUpdateForm;
import com.pickx3.service.WorkImgService;
import com.pickx3.service.WorkService;
import com.pickx3.util.ApiResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

//@Tag(name = "상품 CRUD", description = "상품 관련 API")
@Slf4j
@RequestMapping("/works")
@RestController
public class WorkController {
    @Autowired
    private WorkService workService;

    @Autowired
    private WorkImgService workImgService;

    /*
    * 상품 등록
    * */
    @Operation(summary = "상품 정보 저장", description = "회원은 상품을 등록할 수 있다")
    @PostMapping(consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createWork(@ModelAttribute WorkForm workForm){
        ApiResponseMessage result;
        HashMap data = new HashMap<>();
        try{
            log.info("===========상품명=============" +  workForm.toString().toString());
            Work work = workService.createWork(workForm);
            List<MultipartFile> files = workForm.getFiles();
            workImgService.uploadWorkImg(files, work);

            data.put("workNum",work.getWorkNum());
            data.put("workerNum",work.getUserInfo().getId());
            data.put("workName",work.getWorkName());
            data.put("workPrice",work.getWorkPrice());
            data.put("workDesc",work.getWorkDesc());
//            data.put("workImg", workForm.getFiles());

            result = new ApiResponseMessage(true, "Success", "200", "",data);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            result = new ApiResponseMessage(false, "Error", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 회원별 상품 목록 조회
     * @param workerNum
     * @return
     */
    @Operation(summary = "회원별 상품 목록 조회", description = "회원은 자신이 등록한 상품 목록을 조회 할 수 있다")
    @GetMapping("/users/{workerNum}")
    public ResponseEntity<?> getWorks(@Parameter(description = "회원 고유 ID", required = true, example = "1") @PathVariable(value = "workerNum") Long workerNum){
        ApiResponseMessage result;
        HashMap data = new HashMap<>();
        try{
            List<WorkResponseDTO> works = workService.getWorks(workerNum);
            data.put("works",works);

            result = new ApiResponseMessage(true, "Success", "200", "",data);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            result = new ApiResponseMessage(false, "Error", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 상품 상세정보 조회
     * @param workNum
     * @return
     */
    @Operation(summary = "상품 상세 정보 조회", description = "회원은 상품 상세 정보를 조회할 수 있다")
    @GetMapping("/{workNum}")
    public ResponseEntity<?> getWorkInfo(@PathVariable(value = "workNum") Long workNum){
        ApiResponseMessage result;
        try{
            HashMap data = new HashMap();
            data.put("workInfo", workService.getWorkInfo(workNum));
            result = new ApiResponseMessage(true, "Success" ,"200", "", data );
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new ApiResponseMessage(false, "Error", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
     }


    /**
     * 상품 정보 수정
     * @param workUpdateForm
     * @return
     */
    @Operation(summary = "상품 정보 수정", description = "회원은 상품정보를 수정 할 수 있다")
    @PatchMapping
    public ResponseEntity<?> updateWork(@RequestBody @Valid WorkUpdateForm workUpdateForm){
        ApiResponseMessage result;
        try{
            HashMap data = new HashMap();
            data.put("workInfo", workService.updateWork(workUpdateForm));
            result = new ApiResponseMessage(true, "Success" ,"200", "", data );
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new ApiResponseMessage(false, "Error", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 상품 정보 삭제
     * @param workNum
     * @return
     */
    @Operation(summary = "상품 정보 삭제", description = "회원은 상품정보를 삭제 할 수 있다")
    @DeleteMapping("/{workNum}")
    public ResponseEntity<?> removeWork(@PathVariable(value = "workNum") Long workNum){
        ApiResponseMessage result;
        try{
            workImgService.removeWorkImages(workNum);
            workService.removeWork(workNum);
            result = new ApiResponseMessage(true, "Success" ,"200", "", null );
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new ApiResponseMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
