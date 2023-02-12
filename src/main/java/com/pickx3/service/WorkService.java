package com.pickx3.service;

import com.pickx3.domain.entity.user_package.User;
import com.pickx3.domain.entity.work_package.Work;
import com.pickx3.domain.entity.work_package.dto.work.*;
import com.pickx3.domain.repository.UserRepository;
import com.pickx3.domain.repository.WorkImgRepository;
import com.pickx3.domain.repository.WorkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional
@Service
public class WorkService {
    @Autowired
    private WorkRepository workRepository;
    @Autowired
    private WorkImgRepository workImgRepository;
    @Autowired
    private UserRepository userRepository;

    /*
    * 상품 등록
    * */
    public Work createWork(WorkForm workForm){
        log.info("==========user===========" +workForm.getWorkerNum());
        User user = userRepository.findById(workForm.getWorkerNum()).get();

        Work work = Work.builder()
                .workName(workForm.getWorkName())
                .workDesc(workForm.getWorkDesc())
                .workPrice(workForm.getWorkPrice())
                .userInfo(user)
                .build();

        workRepository.save(work);
        return work;
    }

    /*
     * 회원별 상품 목록 조회
     * */
    public List<WorkResponseDTO> getWorks(Long workerNum){
        List<WorkResponseDTO> dtos = new ArrayList<>();

        List<Work> works = workRepository.findByUserInfo_id(workerNum);
        for(Work work : works){
            WorkResponseDTO dto = new WorkResponseDTO();
            dto.setWorkName(work.getWorkName());
            dto.setWorkDesc(work.getWorkDesc());
            dto.setWorkPrice(work.getWorkPrice());

            List<WorkImgForm> workImgs = workImgRepository.findByWork_workNum(work.getWorkNum());
            dto.setFiles(workImgs);

            dtos.add(dto);

        }
        return dtos;
    }
    
    /*
     * 상품 상세정보 조회
     * */
    public WorkDetailDTO getWorkInfo(Long workNum){
        Work work = workRepository.findById(workNum).get();
        List<WorkImgForm> workImages = workImgRepository.findByWork_workNum(workNum);

        log.info("상품 이미지 정보" + workImages.get(0).getWorkImgName());
        WorkDetailDTO workDetailDTO = new WorkDetailDTO();
        workDetailDTO.setWorkerNum(work.getWorkNum());
        workDetailDTO.setWorkName(work.getWorkName());
        workDetailDTO.setWorkDesc(work.getWorkDesc());
        workDetailDTO.setWorkPrice(work.getWorkPrice());
        workDetailDTO.setWorkImages(workImages);

        return workDetailDTO;
    }

    /*
     * 상품 정보 수정
     * */
    public Work updateWork(WorkUpdateForm workUpdateForm){
        Long workNum = workUpdateForm.getWorkNum();
        String workName = workUpdateForm.getWorkName();
        int workPrice = workUpdateForm.getWorkPrice();
        String workDesc = workUpdateForm.getWorkDesc();

        Work work = workRepository.findById(workNum).get();

        work.updateWork(workName, workPrice, workDesc);

        return work;
    }

    /*
     * 상품 정보 삭제
     * */
    public void removeWork(Long workNum){
        workRepository.deleteById(workNum);
    }
}
