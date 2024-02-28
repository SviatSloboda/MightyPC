package de.mightypc.backend.service.pc;

import de.mightypc.backend.repository.pc.PcRepository;
import org.springframework.stereotype.Service;

@Service
public class PcService {
    private final PcRepository pcRepository;

    public PcService(PcRepository pcRepository) {
        this.pcRepository = pcRepository;
    }
}
