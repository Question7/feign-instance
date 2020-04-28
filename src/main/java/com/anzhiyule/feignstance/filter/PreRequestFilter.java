package com.anzhiyule.feignstance.filter;

import com.anzhiyule.feignstance.request.FeignRequest;

public interface PreRequestFilter {

    void filter(FeignRequest request);
}
