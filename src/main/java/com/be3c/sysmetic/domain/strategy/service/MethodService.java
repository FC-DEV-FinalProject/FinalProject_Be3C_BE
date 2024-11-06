package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.entity.Method;

import java.util.Optional;

public interface MethodService {
    boolean duplCheck(String name);
    Method findById(Long id);
}
