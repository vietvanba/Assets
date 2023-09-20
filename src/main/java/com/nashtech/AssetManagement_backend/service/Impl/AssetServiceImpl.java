package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.AssetDTO;
import com.nashtech.AssetManagement_backend.entity.*;
import com.nashtech.AssetManagement_backend.exception.BadRequestException;
import com.nashtech.AssetManagement_backend.exception.ConflictException;
import com.nashtech.AssetManagement_backend.exception.ResourceNotFoundException;
import com.nashtech.AssetManagement_backend.repository.AssetRepository;
import com.nashtech.AssetManagement_backend.repository.CategoryRepository;
import com.nashtech.AssetManagement_backend.repository.UserRepository;
import com.nashtech.AssetManagement_backend.service.AssetService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@Service
public class AssetServiceImpl implements AssetService {
    private final String CATEGORY_NOT_FOUND_ERROR = "Category prefix not exists.";
    private final String USER_NOT_FOUND_ERROR = "User not exists.";
    private final String ASSET_NOT_FOUND_ERROR = "Asset not found.";
    private final String ASSET_CONFLICT_ERROR = "Asset belongs to one or more historical assignments.";
    private final String ASSET_BAD_STATE_ERROR = "Asset must be AVAILABLE or NOT_AVAILABLE.";

    private final AssetRepository assetRepo;
    private final CategoryRepository categoryRepo;
    private final UserRepository userRepo;

    @Override
    public AssetDTO create(AssetDTO dto, String username) {
        CategoryEntity cate = categoryRepo.findByPrefix(dto.getCategoryPrefix())
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_ERROR));
        LocationEntity location = userRepo.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_ERROR)).getUserDetail().getLocation();
        if (dto.getState() != AssetState.NOT_AVAILABLE && dto.getState() != AssetState.AVAILABLE)
            throw new BadRequestException(ASSET_BAD_STATE_ERROR);
        AssetEntity asset = AssetDTO.toEntity(dto);
        asset.setLocation(location);
        asset.setCategoryEntity(cate);
        return AssetDTO.toDTO(assetRepo.save(asset));
    }

    /// FIND ALL ASSETS
    @Override
    public List<AssetDTO> findAllByAdminLocation(String username) {
        LocationEntity location = userRepo.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_ERROR)).getUserDetail().getLocation();
        List<AssetDTO> AssetDTOs = assetRepo.findAll().stream().filter(p -> p.getLocation().equals(location))
            .map(AssetDTO::toDTO).collect(Collectors.toList());
        AssetDTOs.sort(Comparator.comparing(AssetDTO::getAssetCode));

        return AssetDTOs;
    }

    @Override
    public AssetDTO findByAssetName(String assetName) throws ResourceNotFoundException {
        AssetEntity assetEntity = assetRepo.findByAssetName(assetName).orElseThrow(
                () -> new ResourceNotFoundException("Asset is not found for this asset name:" + assetName));
        return AssetDTO.toDTO(assetEntity);
    }

    @Override
    public AssetDTO findByAssetCode(String assetCode) throws ResourceNotFoundException {
        AssetEntity assetEntity = assetRepo.findByAssetCode(assetCode).orElseThrow(
                () -> new ResourceNotFoundException("Asset is not found for this asset code:" + assetCode));
        return AssetDTO.toDTO(assetEntity);
    }

    @Override
    public Boolean canDelete(String assetCode) {
        return !(assetRepo.findByAssetCode(assetCode).orElseThrow(() -> new ResourceNotFoundException(ASSET_NOT_FOUND_ERROR))
                .getAssignmentEntities().size() > 0);
    }

    @Override
    public Boolean delete(String assetCode) {
        AssetEntity asset = assetRepo.findByAssetCode(assetCode)
                .orElseThrow(() -> new ResourceNotFoundException(ASSET_NOT_FOUND_ERROR));
        if (asset.getAssignmentEntities().size() > 0)
            throw new ConflictException(ASSET_CONFLICT_ERROR);
        assetRepo.deleteById(assetCode);
        return true;
    }

    @Override
    public AssetDTO update(AssetDTO dto) {
        AssetEntity asset = assetRepo.findByAssetCode(dto.getAssetCode())
                .orElseThrow(() -> new ResourceNotFoundException(ASSET_NOT_FOUND_ERROR));
        if (asset.getState() == AssetState.ASSIGNED) {
            throw new ConflictException("Asset has been assigned");
        }

        asset.setAssetName(dto.getAssetName());
        asset.setSpecification(dto.getSpecification());
        asset.setInstalledDate(dto.getInstalledDate());
        asset.setState(dto.getState());
        return AssetDTO.toDTO(assetRepo.save(asset));
    }

    @Override
    public int countByCategory(String prefix, String username) {
        LocationEntity location = userRepo.findByUserName(username).get().getUserDetail().getLocation();
        CategoryEntity category = categoryRepo.findById(prefix).get();
        return assetRepo.countByCategoryEntityAndLocation(category, location);
    }
}
