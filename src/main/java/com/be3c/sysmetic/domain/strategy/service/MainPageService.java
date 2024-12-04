package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MainPageAnalysisDto;
import com.be3c.sysmetic.domain.strategy.dto.MainPageDto;

public interface MainPageService {

    MainPageDto getMain();

    MainPageAnalysisDto getAnalysis(String period);
}
