package com.nashtech.AssetManagement_backend.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.AssetManagement_backend.entity.AssignmentEntity;
import com.nashtech.AssetManagement_backend.entity.AssignmentState;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    private Long id;

    @NotBlank
    private String assetCode;

    @NotBlank
    private String assetName;

    private String category;

    private String specfication;

    @NotBlank
    private String assignedTo;

    @NotBlank
    private String assignedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date assignedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date returnedDate;

    private AssignmentState state;

    private String note;

    private Boolean isCreatedRequest = false;

    public static AssignmentDTO toDTO(AssignmentEntity assign) {
        if (assign == null)
            return null;
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assign.getId());
        dto.setAssetCode(assign.getAssetEntity().getAssetCode());
        dto.setAssetName(assign.getAssetEntity().getAssetName());
        dto.setCategory(assign.getAssetEntity().getCategoryEntity().getName());
        dto.setSpecfication(assign.getAssetEntity().getSpecification());
        dto.setAssignedTo(assign.getAssignTo().getUser().getUserName());
        dto.setAssignedBy(assign.getAssignBy().getUser().getUserName());
        dto.setAssignedDate(assign.getAssignedDate());
//        if (assign.getState().equals(AssignmentState.COMPLETED))
        if(assign.getRequestEntity() != null)
            dto.setReturnedDate(assign.getRequestEntity().getReturnedDate());
        else
            dto.setReturnedDate(null);
        dto.setState(assign.getState());
        dto.setNote(assign.getNote());
        if(assign.getRequestEntity() != null)
            dto.setIsCreatedRequest(true);
        return dto;
    }

    public static AssignmentEntity toEntity(AssignmentDTO dto) {
        if (dto == null)
            return null;
        AssignmentEntity assign = new AssignmentEntity();
        assign.setAssignedDate(dto.getAssignedDate());
        assign.setState(dto.getState());
        assign.setNote(dto.getNote());
        return assign;
    }
}
