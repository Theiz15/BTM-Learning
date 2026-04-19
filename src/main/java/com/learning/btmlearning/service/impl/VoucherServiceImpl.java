package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.dto.request.VoucherRequest;
import com.learning.btmlearning.dto.response.VoucherCourseResponse;
import com.learning.btmlearning.dto.response.VoucherResponse;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.Voucher;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.VoucherMapper;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.repository.VoucherRepository;
import com.learning.btmlearning.service.VoucherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherServiceImpl implements VoucherService {
    VoucherRepository voucherRepository;
    CourseRepository courseRepository;
    VoucherMapper voucherMapper;

    @Override
    public VoucherCourseResponse applyVoucher(String code, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new AppException(ErrorCode.COURSE_NOT_FOUND));

        Voucher voucher = validateAndGetVoucher(code) ;

        BigDecimal discountAmount = course.getPrice().multiply(BigDecimal.valueOf(voucher.getDiscountPercent())).divide(BigDecimal.valueOf(100));

        BigDecimal finalPrice = course.getPrice().subtract(discountAmount);
        
        return VoucherCourseResponse.builder()
                .code(code)
                .discountPercent(voucher.getDiscountPercent())
                .finalPrice(finalPrice)
                .message("Applying voucher")
                .build();
    }

    @Override
    public List<VoucherResponse> getAllVouchers() {
        return voucherRepository.findAll().stream().map(voucherMapper::toResponse).toList();
    }

    @Override
    public VoucherResponse createVoucher(VoucherRequest rq) {
        if (voucherRepository.existsByCode(rq.getCode())){
            throw new AppException(ErrorCode.CODE_ALREADY_EXIST);
        }
        Voucher voucher = voucherMapper.toVoucher(rq) ;

        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }

    @Override
    public VoucherResponse updateVoucher(Long id, VoucherRequest rq) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        voucherMapper.updateVoucher(voucher, rq);
        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }

    @Override
    public void deleteVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        voucher.setIsActive(false);
        voucherRepository.save(voucher);
    }


    public Voucher validateAndGetVoucher(String code) {
        if (code == null || code.isEmpty()) {return null ;}

        Voucher voucher = voucherRepository.findByCodeAndIsActiveTrue(code).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        if (voucher.getStartDate() != null && voucher.getStartDate().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Mã giảm giá chưa đến thời gian sử dụng");
        }

        if (voucher.getExpirationDate() != null && voucher.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }

        if (voucher.getUsedCount() >= voucher.getQuantity()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        }

        return voucher;
    }
}
