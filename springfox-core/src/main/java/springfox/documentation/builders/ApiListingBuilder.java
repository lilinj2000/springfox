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

package springfox.documentation.builders;

import com.google.common.collect.Ordering;
import springfox.documentation.schema.Model;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Authorization;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;

public class ApiListingBuilder {
  private final Ordering<ApiDescription> descriptionOrdering;
  private String apiVersion;
  private String basePath;
  private String resourcePath;
  private String description;
  private int position;

  private Set<String> produces = newHashSet();
  private Set<String> consumes = newHashSet();
  private Set<String> protocol = newHashSet();
  private List<Authorization> authorizations = newArrayList();
  private List<ApiDescription> apis = newArrayList();
  private Map<String, Model> models = newHashMap();

  public ApiListingBuilder(Ordering<ApiDescription> descriptionOrdering) {
    this.descriptionOrdering = descriptionOrdering;
  }

  public ApiListingBuilder apiVersion(String apiVersion) {
    this.apiVersion = BuilderDefaults.defaultIfAbsent(apiVersion, this.apiVersion);
    return this;
  }

  public ApiListingBuilder basePath(String basePath) {
    this.basePath = BuilderDefaults.defaultIfAbsent(basePath, this.basePath);
    return this;
  }

  public ApiListingBuilder resourcePath(String resourcePath) {
    this.resourcePath = BuilderDefaults.defaultIfAbsent(resourcePath, this.resourcePath);
    return this;
  }

  public ApiListingBuilder produces(Set<String> produces) {
    if (produces != null) {
      this.produces = newHashSet(produces);
    }
    return this;
  }

  public ApiListingBuilder consumes(Set<String> consumes) {
    if (consumes != null) {
      this.consumes = newHashSet(consumes);
    }
    return this;
  }

  public ApiListingBuilder appendProduces(List<String> produces) {
    this.produces.addAll(BuilderDefaults.nullToEmptyList(produces));
    return this;
  }

  public ApiListingBuilder appendConsumes(List<String> consumes) {
    this.consumes.addAll(consumes);
    return this;
  }

  public ApiListingBuilder protocols(Set<String> protocols) {
    if (protocols != null) {
      this.protocol.addAll(protocols);
    }
    return this;
  }

  public ApiListingBuilder authorizations(List<Authorization> authorizations) {
    if (authorizations != null) {
      this.authorizations = newArrayList(authorizations);
    }
    return this;
  }

  public ApiListingBuilder apis(List<ApiDescription> apis) {
    if (apis != null) {
      this.apis = descriptionOrdering.sortedCopy(apis);
    }
    return this;
  }

  public ApiListingBuilder models(Map<String, Model> models) {
    if (models != null) {
      this.models.putAll(models);
    }
    return this;
  }

  public ApiListingBuilder description(String description) {
    this.description = BuilderDefaults.defaultIfAbsent(description, this.description);
    return this;
  }

  public ApiListingBuilder position(int position) {
    this.position = position;
    return this;
  }

  public ApiListing build() {
    return new ApiListing(apiVersion, basePath,
            resourcePath, produces, consumes, protocol, authorizations, apis, models, description, position);
  }
}