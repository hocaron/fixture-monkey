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

package com.navercorp.fixturemonkey.javax.validation.validator;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.exception.ValidationFailedException;
import com.navercorp.fixturemonkey.api.validator.ArbitraryValidator;

@API(since = "1.1.15", status = Status.EXPERIMENTAL)
public final class SelectiveJavaxArbitraryValidator implements ArbitraryValidator {
	private Validator validator;
	private final Set<String> bypassValidationFields;

	public SelectiveJavaxArbitraryValidator(Set<String> bypassValidationFields) {
		this.bypassValidationFields = bypassValidationFields;
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			this.validator = factory.getValidator();
		} catch (Exception e) {
			this.validator = null;
		}
	}

	@Override
	public void validate(Object arbitrary) {
		if (this.validator != null) {
			Set<ConstraintViolation<Object>> violations = this.validator.validate(arbitrary);

			// Filter out violations for fields that should bypass validation
			Set<ConstraintViolation<Object>> filteredViolations = violations.stream()
				.filter(violation -> !shouldBypassValidation(violation.getPropertyPath().toString()))
				.collect(Collectors.toSet());

			Set<String> constraintViolationPropertyNames = filteredViolations.stream()
				.map(ConstraintViolation::getPropertyPath)
				.map(Path::toString)
				.collect(Collectors.toSet());

			if (!filteredViolations.isEmpty()) {
				throw new ValidationFailedException(
					"DefaultArbitraryValidator ConstraintViolations. type: " + arbitrary.getClass(),
					constraintViolationPropertyNames
				);
			}
		}
	}

	private boolean shouldBypassValidation(String propertyPath) {
		if (propertyPath == null) {
			return false;
		}
		
		for (String bypassField : bypassValidationFields) {
			if (propertyPath.equals(bypassField) || propertyPath.endsWith("." + bypassField)) {
				return true;
			}
		}
		
		return false;
	}
}