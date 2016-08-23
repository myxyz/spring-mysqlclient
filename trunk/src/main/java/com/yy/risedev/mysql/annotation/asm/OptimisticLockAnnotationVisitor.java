package com.yy.risedev.mysql.annotation.asm;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.SpringAsmInfo;

import com.yy.risedev.mysql.annotation.data.OptimisticLockAnnotation;

class OptimisticLockAnnotationVisitor extends AnnotationVisitor {

	final OptimisticLockAnnotation data;

	public OptimisticLockAnnotationVisitor(OptimisticLockAnnotation data) {
		super(SpringAsmInfo.ASM_VERSION);
		this.data = data;
	}

	@Override
	public void visit(String name, Object value) {
		if ("column".equals(name)) {
			data.column = (String) value;
		}
	}

}
