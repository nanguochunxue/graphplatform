package com.haizhi.graph.dc.inbound.api.validator;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;

/**
 * Created by chengmo on 2018/8/27.
 */
public abstract class AbstractValidator<T> {
    private static final GLog LOG = LogFactory.getLogger(AbstractValidator.class);
    private AbstractValidator nextValidator = null;

    public final boolean validate(T validateBody, ValidatorResult result) {
        boolean flag = this.doValidate(validateBody, result);
        if (this.nextValidator == this) {
            LOG.info("complete all steps validators");
            return flag;
        }
        if (!flag) {
            LOG.info("Failed to validate.");
            return flag;
        }
        if (this.nextValidator != null) {
            flag = flag & this.nextValidator.validate(validateBody, result);
        }
        return flag;
    }

    public AbstractValidator nextValidator(AbstractValidator validator) {
        this.nextValidator = validator;
        return this;
    }

    protected abstract boolean doValidate(T validateBody, ValidatorResult result);
}
