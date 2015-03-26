/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger.web;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger.dto.ApiListing;
import springfox.documentation.swagger.dto.ResourceListing;
import springfox.documentation.swagger.mappers.Mappers;
import springfox.documentation.swagger.mappers.ServiceModelToSwaggerMapper;

import java.util.Map;

@Controller
@ApiIgnore
public class DefaultSwaggerController {


  @Autowired
  private DocumentationCache documentationCache;

  @Autowired
  private ServiceModelToSwaggerMapper mapper;

  @ApiIgnore
  @RequestMapping(value = {SwaggerPluginSupport.DOCUMENTATION_BASE_PATH}, method = RequestMethod.GET)
  public
  @ResponseBody
  ResponseEntity<ResourceListing> getResourceListing(
      @RequestParam(value = "group",  required = false) String swaggerGroup) {

    return getSwaggerResourceListing(swaggerGroup);
  }

  @ApiIgnore
  @RequestMapping(value = {SwaggerPluginSupport.DOCUMENTATION_BASE_PATH + "/{swaggerGroup}/{apiDeclaration}"}, method = RequestMethod.GET)
  public
  @ResponseBody
  ResponseEntity<ApiListing> getApiListing(@PathVariable String swaggerGroup, @PathVariable String apiDeclaration) {
    return getSwaggerApiListing(swaggerGroup, apiDeclaration);
  }

  private ResponseEntity<ApiListing> getSwaggerApiListing(String swaggerGroup, String apiDeclaration) {
    String groupName = Optional.fromNullable(swaggerGroup).or("default");
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<ApiListing>(HttpStatus.NOT_FOUND);
    }
    Map<String, springfox.documentation.service.ApiListing> apiListingMap = documentation.getApiListings();
    Map<String, ApiListing> dtoApiListing
            = Maps.transformEntries(apiListingMap, Mappers.toApiListingDto(mapper));

    ApiListing apiListing = dtoApiListing.get(apiDeclaration);
    return Optional.fromNullable(apiListing)
            .transform(toResponseEntity(ApiListing.class))
            .or(new ResponseEntity<ApiListing>(HttpStatus.NOT_FOUND));
  }

  private ResponseEntity<ResourceListing> getSwaggerResourceListing(String swaggerGroup) {
    String groupName = Optional.fromNullable(swaggerGroup).or("default");
    Documentation documentation = documentationCache.documentationByGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<ResourceListing>(HttpStatus.NOT_FOUND);
    }
    springfox.documentation.service.ResourceListing listing = documentation.getResourceListing();
    ResourceListing resourceListing = mapper.toSwaggerResourceListing(listing);
    return Optional.fromNullable(resourceListing)
            .transform(toResponseEntity(ResourceListing.class))
            .or(new ResponseEntity<ResourceListing>(HttpStatus.NOT_FOUND));
  }

  private <T> Function<T, ResponseEntity<T>> toResponseEntity(Class<T> clazz) {
    return new Function<T, ResponseEntity<T>>() {
      @Override
      public ResponseEntity<T> apply(T input) {
        return new ResponseEntity<T>(input, HttpStatus.OK);
      }
    };
  }
}
