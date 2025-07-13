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

package com.navercorp.fixturemonkey.javax.validation.property;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.navercorp.fixturemonkey.api.property.Property;
import com.navercorp.fixturemonkey.api.property.PropertyGenerator;

@API(since = "1.1.15", status = Status.EXPERIMENTAL)
public final class SelectivePropertyGenerator implements PropertyGenerator {
	private final PropertyGenerator delegate;
	private final Set<String> excludeFields;

	public SelectivePropertyGenerator(PropertyGenerator delegate, Set<String> excludeFields) {
		this.delegate = delegate;
		this.excludeFields = excludeFields;
	}

	@Override
	public List<Property> generateChildProperties(Property property) {
		List<Property> childProperties = delegate.generateChildProperties(property);
		
		// 제외할 필드들을 필터링
		return childProperties.stream()
			.filter(childProperty -> !shouldExcludeProperty(childProperty))
			.collect(Collectors.toList());
	}

	private boolean shouldExcludeProperty(Property property) {
		String propertyName = property.getName();
		return propertyName != null && excludeFields.contains(propertyName);
	}
}