package com.penglab.hi5.core.ui.ImageClassify;

import java.util.Objects;

public class ClassifySolutionInfo {
    public String solutionName;
    public String solutionDetail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassifySolutionInfo that = (ClassifySolutionInfo) o;
        return Objects.equals(solutionName, that.solutionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(solutionName);
    }
}
