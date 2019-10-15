package com.haizhi.graph.engine.base.rule.logic;

import com.haizhi.graph.common.constant.LogicOperator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengmo on 2018/7/23.
 */
@Data
@NoArgsConstructor
public class LogicRule {
    private String expression;
    private List<LogicOperator> logicOperators = new ArrayList<>();
    private List<LogicRule> rules = new ArrayList<>();

    public LogicRule(String expression) {
        this.expression = expression;
    }

    public void addLogicOperator(LogicOperator operator){
        this.logicOperators.add(operator);
    }

    public void addRule(LogicRule rule){
        this.rules.add(rule);
    }

    public boolean hasLogicOperators(){
        if (CollectionUtils.isEmpty(logicOperators)){
            return false;
        }
        if (CollectionUtils.isEmpty(logicOperators)){
            return false;
        }
        return rules.size() == logicOperators.size() + 1;
    }
}
