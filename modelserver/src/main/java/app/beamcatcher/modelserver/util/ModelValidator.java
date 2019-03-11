package app.beamcatcher.modelserver.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelValidator {
	private static final Logger logger = LoggerFactory.getLogger(ModelValidator.class);

	private static volatile Validator INSTANCE = Validation.buildDefaultValidatorFactory().getValidator();

	private ModelValidator() {
	}

	public static void validate(final Object pObject) {

		final Set<ConstraintViolation<Object>> setConstraintViolation = ModelValidator.INSTANCE.validate(pObject);
		final Integer numberOfViolations = setConstraintViolation.size();
		if (numberOfViolations > 0) {
			logger.error("Validation error on object !!");
			logger.error("Class: " + pObject.getClass());
			logger.error("String representation:" + pObject);
			for (ConstraintViolation<Object> constraintViolation : setConstraintViolation) {
				final String message = constraintViolation.getMessage();
				final Object rootBean = constraintViolation.getRootBean();
				final Path propertyPath = constraintViolation.getPropertyPath();
				final Object leafBean = constraintViolation.getLeafBean();
				final Object invalidValue = constraintViolation.getInvalidValue();
				logger.error("rootBean: " + rootBean);
				logger.error("leafBean: " + leafBean);
				logger.error("propertyPath: " + propertyPath);
				logger.error("message: " + message);
				logger.error("value: " + invalidValue);
			}
			logger.error("Exiting !!");
			System.exit(-1);
		}
	}

}
