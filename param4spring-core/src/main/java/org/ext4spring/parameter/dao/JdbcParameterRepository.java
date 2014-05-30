/*******************************************************************************
 * Copyright 2013 the original author
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.ext4spring.parameter.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.ext4spring.parameter.model.ParameterMetadata;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcParameterRepository extends AbstractParameterRepository {

    private final String DEFAULT_COUNT_QUERY = "SELECT count(*) FROM parameters WHERE domain=:domain AND parameter=:parameter";
    private final String DEFAULT_SELECT_QUERY = "SELECT data FROM parameters WHERE domain=:domain AND parameter=:parameter";
    private final String DEFAULT_INSERT_STATEMENT = "INSERT INTO parameters(domain, parameter, data) VALUES(:domain,:parameter,:data)";
    private final String DEFAULT_UPDATE_STATEMENT = "UPDATE parameters SET data=:data WHERE domain=:domain AND parameter=:parameter";
    private final String DEFAULT_QUALIFIER_QUERY = "SELECT parameters.PARAMETER FROM parameters WHERE DOMAIN=:domain AND PARAMETER LIKE :parameterPrefix";
    private final String DEFAULT_DELETE_STATEMENT = "DELETE FROM parameters WHERE domain=:domain AND parameter=:parameter";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private String countQuery = DEFAULT_COUNT_QUERY;
    private String selectQuery = DEFAULT_SELECT_QUERY;
    private String insertStatement = DEFAULT_INSERT_STATEMENT;
    private String updateStatement = DEFAULT_UPDATE_STATEMENT;
    private String qualifierQuery = DEFAULT_QUALIFIER_QUERY;
    private String deleteStatement = DEFAULT_DELETE_STATEMENT;

    @Required
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public boolean parameterExists(ParameterMetadata metadata) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("domain", metadata.getDomain());
        namedParameters.addValue("parameter", metadata.getFullParameterName());
        return (namedParameterJdbcTemplate.queryForInt(this.countQuery, namedParameters) > 0);
    }

    @Override
    public List<String> getParameterQualifiers(ParameterMetadata metadata) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("domain", metadata.getDomain());
        namedParameters.addValue("parameterPrefix", metadata.getParameter()+".%");
        List<String> paramsWithQualifier=namedParameterJdbcTemplate.queryForList(this.qualifierQuery, namedParameters, String.class);
        List<String> qualifiers=new ArrayList<String>();
        for (String paramFullName:paramsWithQualifier) {
            qualifiers.add(ParameterMetadata.parseQualifier(paramFullName, metadata.getParameter()));
        }
        return qualifiers;
    }

    @Override
    public String getValue(ParameterMetadata metadata) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("domain", metadata.getDomain());
        namedParameters.addValue("parameter", metadata.getFullParameterName());
        try {
            return namedParameterJdbcTemplate.queryForObject(this.selectQuery, namedParameters, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void setValue(ParameterMetadata metadata, String value) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("domain", metadata.getDomain());
        parameters.put("parameter", metadata.getFullParameterName());
        parameters.put("data", value);
        if (this.parameterExists(metadata)) {
            this.namedParameterJdbcTemplate.update(updateStatement, parameters);
        } else {
            this.namedParameterJdbcTemplate.update(insertStatement, parameters);
        }
    }

    @Override
    public void delete(ParameterMetadata metadata) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("domain", metadata.getDomain());
        parameters.put("parameter", metadata.getFullParameterName());
        this.namedParameterJdbcTemplate.update(this.deleteStatement, parameters);
    }

    public void setCountQuery(String countQuery) {
        this.countQuery = countQuery;
    }

    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }

    public void setInsertStatement(String insertStatement) {
        this.insertStatement = insertStatement;
    }

    public void setUpdateStatement(String updateStatement) {
        this.updateStatement = updateStatement;
    }

    public void setQualifierQuery(String qualifierQuery) {
        this.qualifierQuery = qualifierQuery;
    }

    public void setDeleteStatement(String deleteStatement) {
        this.deleteStatement = deleteStatement;
    }
    
    
}
