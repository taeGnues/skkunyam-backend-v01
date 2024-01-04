package com.skkudteam3.skkusirenorder.src.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TakeOutCheckGetResDTO {
    private Long takeOut;
    private Long dineIn;

    public TakeOutCheckGetResDTO(Long takeOut, Long dineIn) {
        this.takeOut = takeOut;
        this.dineIn = dineIn;
    }
}
