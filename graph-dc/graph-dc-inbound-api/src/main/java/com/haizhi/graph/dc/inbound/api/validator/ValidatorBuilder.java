package com.haizhi.graph.dc.inbound.api.validator;

import com.haizhi.graph.common.context.SpringContext;

/**
 * Created by chengmo on 2018/8/27.
 */
public class ValidatorBuilder {
    private AbstractValidator head = null;
    private AbstractValidator tail = null;

    public ValidatorBuilder add(Class<? extends AbstractValidator> validatorClass) {
        AbstractValidator validator = SpringContext.getBean(validatorClass);
        if (head == null) {
            head = validator;
            tail = validator;
        } else {
            tail.nextValidator(validator);
            tail = validator;
        }
        return this;
    }

    public AbstractValidator get() {
        return head;
    }

    public static ValidatorBuilder build() {
        return new ValidatorBuilder();
    }
}
