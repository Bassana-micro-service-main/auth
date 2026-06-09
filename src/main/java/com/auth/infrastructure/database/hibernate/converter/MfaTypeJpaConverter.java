package com.auth.infrastructure.database.hibernate.converter;

import com.auth.infrastructure.database.hibernate.enums.MfaType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MfaTypeJpaConverter implements AttributeConverter<MfaType, String> {

	@Override
	public String convertToDatabaseColumn(MfaType attribute) {
		return attribute == null ? null : attribute.getValue();
	}

	@Override
	public MfaType convertToEntityAttribute(String dbData) {
		return dbData == null ? null : MfaType.fromValue(dbData);
	}
}
