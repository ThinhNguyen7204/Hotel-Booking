package com.aplusplus.HotelBooking.mapper;

import com.aplusplus.HotelBooking.dto.FacilityDTO;
import com.aplusplus.HotelBooking.model.Facility;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class FacilityMapper {
    public abstract FacilityDTO facilityToFacilityDTO(Facility facility);
    public abstract Facility facilityDTOToFacility(FacilityDTO facilityDTO);
}
