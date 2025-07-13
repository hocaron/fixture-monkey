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

package com.navercorp.fixturemonkey.tests.java;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.javax.validation.introspector.SelectiveValidationConstraintGenerator;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;

class SelectiveConstraintGeneratorTest {

	@ToString
	public static class SimpleTestClass {
		@NotNull
		private String fieldA;
		@NotEmpty
		private String fieldB;
		@NotNull
		private String fieldC;

		public SimpleTestClass() {}

		public String getFieldA() { return fieldA; }
		public void setFieldA(String fieldA) { this.fieldA = fieldA; }
		public String getFieldB() { return fieldB; }
		public void setFieldB(String fieldB) { this.fieldB = fieldB; }
		public String getFieldC() { return fieldC; }
		public void setFieldC(String fieldC) { this.fieldC = fieldC; }
	}

	@Test
	void selectiveValidationWithJavaxValidationPlugin() {
		HashSet<String> bypassFields = new HashSet<String>() {{ add("fieldB"); }};

		FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
			.objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
			// JavaxValidationPlugin 대신 필요한 부분만 직접 설정
			.javaConstraintGenerator(new SelectiveValidationConstraintGenerator(bypassFields))
			.defaultNotNull(true)
			.build();

		SimpleTestClass request = fixtureMonkey.giveMeBuilder(SimpleTestClass.class)
			.set("fieldA", null)
			.set("fieldB", "")
			.sample();

		System.out.println(request);
	}
}
