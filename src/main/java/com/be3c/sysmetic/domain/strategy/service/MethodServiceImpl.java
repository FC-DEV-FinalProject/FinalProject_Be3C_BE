package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.global.common.Code;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MethodServiceImpl implements MethodService {

    private final MethodRepository methodRepository;

    @Override
    public boolean duplCheck(String name) {
        return methodRepository.findByName(name).isEmpty();
    }

    @Override
    public Method findById(Long id) throws NullPointerException {
        return methodRepository.findByIdAndStatusCode(id, Code.USING_STATE.getCode()).get();
    }
}
