package com.learning.btmlearning.service;

import com.learning.btmlearning.dto.request.VoucherRequest;
import com.learning.btmlearning.dto.response.VoucherCourseResponse;
import com.learning.btmlearning.dto.response.VoucherResponse;

import java.util.List;

public interface VoucherService {
//    VoucherResponse createVoucher()
    VoucherCourseResponse applyVoucher(String code , Long courseId) ;

    List<VoucherResponse> getAllVouchers() ;

    VoucherResponse createVoucher(VoucherRequest rq) ;

    VoucherResponse updateVoucher(Long id , VoucherRequest rq) ;

    void deleteVoucher(Long id) ;
}
