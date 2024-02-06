package de.mightypc.backend.service;

import de.mightypc.backend.repository.PcRepository;
import org.springframework.stereotype.Service;

@Service
public class PcService {
    private final PcRepository pcRepository;

    public PcService(PcRepository pcRepository){
        this.pcRepository = pcRepository;
    }
}
