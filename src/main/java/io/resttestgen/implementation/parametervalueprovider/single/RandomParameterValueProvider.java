package io.resttestgen.implementation.parametervalueprovider.single;

import io.resttestgen.core.Environment;
import io.resttestgen.core.datatype.parameter.*;
import io.resttestgen.core.helper.ExtendedRandom;
import io.resttestgen.core.testing.parametervalueprovider.ParameterValueProvider;

/**
 * Generates a random value for the given parameter.
 */
public class RandomParameterValueProvider extends ParameterValueProvider {

    private static final ExtendedRandom random = Environment.getInstance().getRandom();

    @Override
    public Object provideValueFor(ParameterLeaf parameterLeaf) {

        if (parameterLeaf instanceof StringParameter) {
            return generateCompliantString((StringParameter) parameterLeaf);
        } else if (parameterLeaf instanceof NumberParameter) {
            return generateCompliantNumber((NumberParameter) parameterLeaf);
        } else if (parameterLeaf instanceof BooleanParameter) {
            return random.nextBoolean();
        } else if (parameterLeaf instanceof NullParameter) {
            return null;
        } else {
            return parameterLeaf.generateCompliantValue();
        }
    }

    private String generateCompliantString(StringParameter parameter) {
        // FIXME: move here generation of value
        return (String) parameter.generateCompliantValue();
    }

    private Number generateCompliantNumber(NumberParameter parameter) {

        // Get the actual format, or infer it
        ParameterTypeFormat format = parameter.inferFormat();

        // With 0.5 probability, restrict the range of the generated value. This is done because values in the
        // restricted range (0 - 120 in this case) are used more commonly than other random values.
        boolean restrictRange = random.nextBoolean();
        double RESTRICTED_MIN = 0.;
        double RESTRICTED_MAX = 120.;

        // If the parameter is a double
        if (format == ParameterTypeFormat.DOUBLE) {

            // Set min and max value, if defined
            double min = parameter.getMinimum() != null ? parameter.getMinimum() : -Double.MAX_VALUE;
            double max = parameter.getMaximum() != null ? parameter.getMaximum() : Double.MAX_VALUE;

            // If min is not less than max, reset one of the two variables randomly
            if (min > max) {
                if (random.nextBoolean()) {
                    min = -Double.MAX_VALUE;
                } else {
                    max = Double.MAX_VALUE;
                }
            }

            // Exclude values if minimum or maximum are exclusive
            min = parameter.isExclusiveMinimum() ? min + Double.MIN_VALUE : min;
            max = parameter.isExclusiveMaximum() ? max - Double.MIN_VALUE : max;

            // Restrict the boundaries
            min = restrictRange && RESTRICTED_MIN > min ? RESTRICTED_MIN : min;
            max = restrictRange && RESTRICTED_MAX < max ? RESTRICTED_MAX : max;

            // Generate and return the value
            return random.nextDouble(min, max);
        }

        // If the parameter is a float
        else if (format == ParameterTypeFormat.FLOAT) {

            // Set min and max value, if defined
            float min = parameter.getMinimum() != null ? parameter.getMinimum().floatValue() : -Float.MAX_VALUE;
            float max = parameter.getMaximum() != null ? parameter.getMaximum().floatValue() : Float.MAX_VALUE;

            // If min is not less than max, reset one of the two variables randomly
            if (min > max) {
                if (random.nextBoolean()) {
                    min = -Float.MAX_VALUE;
                } else {
                    max = Float.MAX_VALUE;
                }
            }

            // Exclude values if minimum or maximum are exclusive
            min = parameter.isExclusiveMinimum() ? min + Float.MIN_VALUE : min;
            max = parameter.isExclusiveMaximum() ? max - Float.MIN_VALUE : max;

            // Restrict the boundaries
            min = restrictRange && (float) RESTRICTED_MIN > min ? (float) RESTRICTED_MIN : min;
            max = restrictRange && (float) RESTRICTED_MAX < max ? (float) RESTRICTED_MAX : max;

            return random.nextFloat(min, max);
        }

        // If the parameter is an integer or long
        else {

            boolean isLong = true; // Is the parameter a long or an integer?

            // Default
            long min = Long.MIN_VALUE;
            long max = Long.MAX_VALUE;

            // Changed based on the format
            switch (format) {
                case INT8:
                    min = -128;
                    max = 127;
                    isLong = false;
                    break;
                case INT16:
                    min = -32768;
                    max = 32767;
                    isLong = false;
                    break;
                case INT32:
                    min = Integer.MIN_VALUE;
                    max = Integer.MAX_VALUE;
                    isLong = false;
                    break;
                case UINT8:
                    min = 0;
                    max = 255;
                    isLong = false;
                    break;
                case UINT16:
                    min = 0;
                    max = 65535;
                    isLong = false;
                    break;
                case UINT32:
                    min = 0;
                    isLong = false;
                    max = 4294967295L;
                    break;
                case UINT64:
                    min = 0;
                    break;
            }

            // Set min and max value, if defined
            min = parameter.getMinimum() != null ? parameter.getMinimum().longValue() : min;
            max = parameter.getMaximum() != null ? parameter.getMaximum().longValue() : max;

            // If min is not less than max, reset one of the two variables randomly
            if (min > max) {
                if (random.nextBoolean()) {
                    min = Long.MIN_VALUE;
                } else {
                    max = Long.MAX_VALUE;
                }
            }

            // Exclude values if minimum or maximum are exclusive
            min = parameter.isExclusiveMinimum() ? min + 1 : min;
            max = parameter.isExclusiveMaximum() ? max - 1 : max;

            // Restrict the boundaries
            min = restrictRange && (long) RESTRICTED_MIN > min ? (long) RESTRICTED_MIN : min;
            max = restrictRange && (long) RESTRICTED_MAX < max ? (long) RESTRICTED_MAX : max;

            if (isLong) {
                return random.nextLong(min, max);
            } else {
                return random.nextInt((int) min, (int) max);
            }
        }
    }
}
