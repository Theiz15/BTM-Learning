package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.request.VoucherRequest;
import com.learning.btmlearning.dto.response.VoucherResponse;
import com.learning.btmlearning.entity.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    VoucherResponse toResponse(Voucher voucher);

    Voucher toVoucher(VoucherRequest request);

    void updateVoucher(@MappingTarget Voucher voucher, VoucherRequest request);

}
