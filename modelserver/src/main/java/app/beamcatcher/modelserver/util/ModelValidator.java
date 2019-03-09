package app.beamcatcher.modelserver.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;

public class ModelValidator {

	private static volatile Validator INSTANCE = Validation.buildDefaultValidatorFactory().getValidator();

	private ModelValidator() {
	}

	public static void validate(final Object pObject) {

		final Set<ConstraintViolation<Object>> setConstraintViolation = ModelValidator.INSTANCE.validate(pObject);
		final Integer numberOfViolations = setConstraintViolation.size();
		if (numberOfViolations > 0) {
			LogHelper.logError("Validation error on object !!");
			LogHelper.logError("Class: " + pObject.getClass());
			LogHelper.logError("String representation:" + pObject);
			for (ConstraintViolation<Object> constraintViolation : setConstraintViolation) {
				final String message = constraintViolation.getMessage();
				final Object rootBean = constraintViolation.getRootBean();
				final Path propertyPath = constraintViolation.getPropertyPath();
				final Object leafBean = constraintViolation.getLeafBean();
				final Object invalidValue = constraintViolation.getInvalidValue();
				LogHelper.logError("rootBean: " + rootBean);
				LogHelper.logError("leafBean: " + leafBean);
				LogHelper.logError("propertyPath: " + propertyPath);
				LogHelper.logError("message: " + message);
				LogHelper.logError("value: " + invalidValue);
			}
			LogHelper.logError("Exiting !!");
			System.exit(-1);
		}
	}

}
