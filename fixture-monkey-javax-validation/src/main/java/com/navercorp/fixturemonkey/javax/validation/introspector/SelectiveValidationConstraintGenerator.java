/*
 * Fixture Monkey
 *
 * Copyright (c) 2021-present NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.fixturemonkey.javax.validation.introspector;

import com.navercorp.fixturemonkey.api.constraint.*;
import com.navercorp.fixturemonkey.api.generator.ArbitraryGeneratorContext;
import com.navercorp.fixturemonkey.api.property.PropertyPath;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import javax.annotation.Nullable;
import java.util.Set;

@API(since = "1.1.15", status = Status.EXPERIMENTAL)
public final class SelectiveValidationConstraintGenerator implements JavaConstraintGenerator {
	private final JavaConstraintGenerator delegate;
	private final Set<String> bypassValidationFields;

	public SelectiveValidationConstraintGenerator(Set<String> bypassValidationFields) {
		this.delegate = new JavaxValidationConstraintGenerator();
		this.bypassValidationFields = bypassValidationFields;
	}

	public SelectiveValidationConstraintGenerator(JavaConstraintGenerator delegate, Set<String> bypassValidationFields) {
		this.delegate = delegate;
		this.bypassValidationFields = bypassValidationFields;
	}

	@Override
	@Nullable
	public JavaStringConstraint generateStringConstraint(ArbitraryGeneratorContext context) {
		if (shouldBypassValidation(context)) {
			return null;
		}
		return delegate.generateStringConstraint(context);
	}

	@Override
	@Nullable
	public JavaIntegerConstraint generateIntegerConstraint(ArbitraryGeneratorContext context) {
		if (shouldBypassValidation(context)) {
			return null;
		}
		return delegate.generateIntegerConstraint(context);
	}

	@Override
	@Nullable
	public JavaDecimalConstraint generateDecimalConstraint(ArbitraryGeneratorContext context) {
		if (shouldBypassValidation(context)) {
			return null;
		}
		return delegate.generateDecimalConstraint(context);
	}

	@Override
	@Nullable
	public JavaContainerConstraint generateContainerConstraint(ArbitraryGeneratorContext context) {
		if (shouldBypassValidation(context)) {
			return null;
		}
		return delegate.generateContainerConstraint(context);
	}

	@Override
	@Nullable
	public JavaDateTimeConstraint generateDateTimeConstraint(ArbitraryGeneratorContext context) {
		if (shouldBypassValidation(context)) {
			return null;
		}
		return delegate.generateDateTimeConstraint(context);
	}

	private boolean shouldBypassValidation(ArbitraryGeneratorContext context) {
		try {
			// Property의 이름을 직접 확인
			String propertyName = context.getResolvedProperty().getName();
			if (propertyName != null && bypassValidationFields.contains(propertyName)) {
				return true;
			}

			// PropertyPath를 통한 확인도 시도
			PropertyPath propertyPath = context.getPropertyPath();
			if (propertyPath != null) {
				String fullPath = propertyPath.toString();
				if (fullPath != null) {
					for (String bypassField : bypassValidationFields) {
						if (fullPath.endsWith("." + bypassField) || fullPath.equals(bypassField)) {
							return true;
						}
					}
				}

				// PropertyPath의 마지막 segment 확인
				if (propertyPath.getProperty() != null) {
					String lastPropertyName = propertyPath.getProperty().getName();
					if (lastPropertyName != null && bypassValidationFields.contains(lastPropertyName)) {
						return true;
					}
				}
			}

			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
