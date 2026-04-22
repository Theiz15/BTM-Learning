package com.learning.btmlearning.controller;


import com.learning.btmlearning.dto.request.VoucherRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.VoucherCourseResponse;
import com.learning.btmlearning.dto.response.VoucherResponse;
import com.learning.btmlearning.service.VoucherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/vouchers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class VoucherController {
    VoucherService voucherService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<VoucherResponse>> getAllVouchers() {
        return ApiResponse.<List<VoucherResponse>>builder()
                .message("Get all vouchers successfully")
                .result(voucherService.getAllVouchers())
                .build();
    }

    @GetMapping("/apply")
    public ApiResponse<VoucherCourseResponse> applyVoucher(
            @RequestParam String code,
            @RequestParam Long courseId) {
        return ApiResponse.<VoucherCourseResponse>builder()
                .message("Successfully updated profile")
                .result(voucherService.applyVoucher(code,courseId))
                .build();
    }

    @PostMapping()
    public ApiResponse<VoucherResponse> createVoucher(@RequestBody VoucherRequest request) {
        return ApiResponse.<VoucherResponse>builder()
                .message("Create voucher successfully")
                .result(voucherService.createVoucher(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<VoucherResponse> updateVoucher(@PathVariable Long id, @RequestBody VoucherRequest request) {
        return ApiResponse.<VoucherResponse>builder()
                .message("Create voucher successfully")
                .result(voucherService.updateVoucher(id ,request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ApiResponse.<Void>builder()
                .message("Deactivate voucher successfully")
                .build();
    }


}
