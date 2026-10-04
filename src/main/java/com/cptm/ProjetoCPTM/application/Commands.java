package com.cptm.ProjetoCPTM.application;

import com.cptm.ProjetoCPTM.domain.Network.*;
import jakarta.validation.constraints.*;

public final class Commands {
    private Commands() {}
    public record TrainInput(@NotBlank @Size(max=100) String id, @NotBlank @Size(max=40) String number,
            @NotBlank @Size(max=120) String model, @NotBlank String profileId,
            @Min(1) @Max(10000) int capacity, @Min(1) @Max(30) int carriages,
            @NotBlank String lineId, @NotBlank String nodeId, boolean active) {}
    public record Active(boolean active) {}
    public record TransferInput(@NotBlank String targetLineId, String destinationNodeId, Long expectedVersion) {}
    public record IncidentInput(@NotBlank @Size(max=120) String label, @NotBlank @Size(max=2000) String description,
            @NotBlank @Size(max=60) String category, @NotNull Severity severity,
            @NotNull Target targetType, @NotBlank String targetId, @NotNull Effect effect) {}
    public record ClockInput(boolean running, @DecimalMin("0.1") @DecimalMax("60") double timeScale) {}
    public record StepInput(@DecimalMin("0.1") @DecimalMax("60") double seconds) {}
}
