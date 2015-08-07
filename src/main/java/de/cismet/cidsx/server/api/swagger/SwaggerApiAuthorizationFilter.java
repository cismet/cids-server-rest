/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cidsx.server.api.swagger;

import io.swagger.core.filter.SwaggerSpecFilter;
import io.swagger.model.ApiDescription;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;

import java.util.List;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class SwaggerApiAuthorizationFilter implements SwaggerSpecFilter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isOperationAllowed(final Operation operation,
            final ApiDescription api,
            final Map<String, List<String>> params,
            final Map<String, String> cookies,
            final Map<String, List<String>> headers) {
        return true;
    }

    @Override
    public boolean isParamAllowed(final Parameter parameter,
            final Operation operation,
            final ApiDescription api,
            final Map<String, List<String>> params,
            final Map<String, String> cookies,
            final Map<String, List<String>> headers) {
        return !((parameter.getAccess() != null) && parameter.getAccess().equalsIgnoreCase("internal"));
    }

    @Override
    public boolean isPropertyAllowed(final Model model,
            final Property property,
            final String propertyName,
            final Map<String, List<String>> params,
            final Map<String, String> cookies,
            final Map<String, List<String>> headers) {
        return true;
    }
}
