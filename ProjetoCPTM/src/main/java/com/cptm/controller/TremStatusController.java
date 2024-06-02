package com.cptm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.model.MudaStatusTrem;

@RestController
@RequestMapping("/trem")
public class TremStatusController implements MudaStatusTrem {

    @GetMapping("/mudaStatus")
    public int mudaStatusTrem(@RequestParam boolean situacao) {
        return MudaStatusTrem.MudaStatusTrem(situacao);
    }
}
