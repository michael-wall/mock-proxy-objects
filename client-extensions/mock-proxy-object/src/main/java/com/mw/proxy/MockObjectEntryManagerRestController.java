/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.mw.proxy;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.datafaker.Faker;
import net.datafaker.providers.base.Name;

/**
 * @author Feliphe Marinho
 * @author Brian Wing Shun Chan
 */
@RequestMapping("/object/entry/manager/mock")
@RestController
public class MockObjectEntryManagerRestController extends BaseRestController {

	@DeleteMapping(
		"/{objectDefinitionExternalReferenceCode}/{externalReferenceCode}"
	)
	public ResponseEntity<String> delete(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String objectDefinitionExternalReferenceCode,
		@PathVariable String externalReferenceCode,
		@RequestParam Map<String, String> parameters) {

		log(jwt, _log, parameters);

		Map<String, JSONObject> objectEntryJSONObjects =
			_getObjectEntryJSONObjects(objectDefinitionExternalReferenceCode);

		JSONObject objectEntryJSONObject = objectEntryJSONObjects.remove(
			externalReferenceCode);

		if (objectEntryJSONObject == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(
			objectEntryJSONObject.toString(), HttpStatus.OK);
	}
	
	private static void sortJsonList(List<JSONObject> list, String fieldName, boolean ascending) {
	    list.sort((o1, o2) -> {
	        String v1 = o1.optString(fieldName, "");	        
	        String v2 = o2.optString(fieldName, "");
	        int result = v1.compareToIgnoreCase(v2);
	        return ascending ? result : -result;
	    });
	}	

	@GetMapping("/{objectDefinitionExternalReferenceCode}")
	public ResponseEntity<String> get(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String objectDefinitionExternalReferenceCode,
		@RequestParam Map<String, String> parameters) {

		log(jwt, _log, parameters);
		
		
		String searchString = parameters.get("search");
		String filterString = parameters.get("filter");
		String sortString = parameters.get("sort");
		String pageString = parameters.get("page");
		String pageSizeString = parameters.get("pageSize");
		
		String sortField = "externalReferenceCode"; // Should always be present, use as fallback...
		boolean sortOrder = true;
		
		if (sortString != null && !sortString.equalsIgnoreCase("")) {
			String sortArray[] = sortString.split(":");
			
			if (sortArray != null && sortArray.length == 2) {
				if (sortArray[0] != null && !sortArray[0].equalsIgnoreCase("")) {
					sortField = sortArray[0];
				}
				
				if (sortArray[1] != null && sortArray[1].equalsIgnoreCase("desc")) {
					sortOrder = false;
				}
			}			
		}
		
		int page = Integer.parseInt(pageString); // 1 based
		int pageSize = Integer.parseInt(pageSizeString);
		
		int start = (page - 1) * pageSize;
		int end = start + pageSize;
		
		Map<String, JSONObject> objectEntryJSONObjects = _getObjectEntryJSONObjects(objectDefinitionExternalReferenceCode);

		List<JSONObject> allItemsList = new ArrayList<JSONObject>();
		List<JSONObject> searchedItemsList = new ArrayList<JSONObject>();
		List<JSONObject> pageItemsList = new ArrayList<JSONObject>();
		
		allItemsList.addAll(objectEntryJSONObjects.values());
		
		//Sort first
		sortJsonList(allItemsList, sortField, sortOrder);
		
		//Primitive search next
		if (searchString != null && !searchString.equalsIgnoreCase("")) {
			for (JSONObject item: allItemsList) {
				String itemString = item.toString();
				
				if (itemString.toLowerCase().indexOf(searchString.toLowerCase()) >= 0) {
					searchedItemsList.add(item);	
				}
			}
		} else {
			searchedItemsList.addAll(allItemsList);
		}
		
		System.out.println("listSize: " + searchedItemsList.size());
		
		//Pagination last
		// Reset if needed...
		if (start > searchedItemsList.size()) {
			start = 0;
			end = start + pageSize - 1;
		}
		
		System.out.println("initial start: " + start + ", end: " + end);
		
		if (end > searchedItemsList.size()) end = searchedItemsList.size();
		if (end < 0) end = 0;
		
		System.out.println("updated start: " + start + ", end: " + end);
		
		if (searchedItemsList.size() > 0) {
			pageItemsList = searchedItemsList.subList(start, end);	
		}
		
		return new ResponseEntity<>(
			new JSONObject(
			).put(
				"items", pageItemsList
			).put(
				"totalCount", searchedItemsList.size()
			).toString(),
			HttpStatus.OK);
	}

	@GetMapping(
		"/{objectDefinitionExternalReferenceCode}/{externalReferenceCode}"
	)
	public ResponseEntity<String> get(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String objectDefinitionExternalReferenceCode,
		@PathVariable String externalReferenceCode,
		@RequestParam Map<String, String> parameters) {

		log(jwt, _log, parameters);

		Map<String, JSONObject> objectEntryJSONObjects =
			_getObjectEntryJSONObjects(objectDefinitionExternalReferenceCode);

		JSONObject objectEntryJSONObject = objectEntryJSONObjects.get(
			externalReferenceCode);

		if (objectEntryJSONObject == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(
			objectEntryJSONObject.toString(), HttpStatus.OK);
	}

	@PostMapping("/{objectDefinitionExternalReferenceCode}")
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String objectDefinitionExternalReferenceCode,
		@RequestBody String json) {

		log(jwt, _log, json);

		Map<String, JSONObject> objectEntryJSONObjects =
			_getObjectEntryJSONObjects(objectDefinitionExternalReferenceCode);

		JSONObject objectEntryJSONObject = _getObjectEntryJSONObject(json);

		if (objectEntryJSONObject.isNull("creator")) {
			Faker faker = new Faker();

			Name name = faker.name();

			objectEntryJSONObject.put(
				"creator", Collections.singletonMap("name", name.fullName()));
		}

		String externalReferenceCode =
			!objectEntryJSONObject.isNull("externalReferenceCode") ?
				objectEntryJSONObject.getString("externalReferenceCode") : null;

		if ((externalReferenceCode == null) ||
			externalReferenceCode.isEmpty()) {

			externalReferenceCode = String.valueOf(UUID.randomUUID());

			objectEntryJSONObject.put(
				"externalReferenceCode", externalReferenceCode);
		}

		if (objectEntryJSONObjects.containsKey(externalReferenceCode)) {
			return new ResponseEntity<>(json, HttpStatus.CONFLICT);
		}

		objectEntryJSONObjects.put(
			externalReferenceCode, objectEntryJSONObject);

		return new ResponseEntity<>(
			objectEntryJSONObject.toString(), HttpStatus.OK);
	}

	@PutMapping(
		"/{objectDefinitionExternalReferenceCode}/{externalReferenceCode}"
	)
	public ResponseEntity<String> put(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable String objectDefinitionExternalReferenceCode,
		@PathVariable String externalReferenceCode, @RequestBody String json) {

		log(jwt, _log, json);

		Map<String, JSONObject> objectEntryJSONObjects =
			_getObjectEntryJSONObjects(objectDefinitionExternalReferenceCode);

		if (!objectEntryJSONObjects.containsKey(externalReferenceCode)) {
			return new ResponseEntity<>(json, HttpStatus.NOT_FOUND);
		}

		JSONObject objectEntryJSONObject = _getObjectEntryJSONObject(json);

		objectEntryJSONObjects.put(
			externalReferenceCode, objectEntryJSONObject);

		return new ResponseEntity<>(
			objectEntryJSONObject.toString(), HttpStatus.OK);
	}

	private JSONObject _getObjectEntryJSONObject(String json) {
		JSONObject jsonObject = new JSONObject(json);

		JSONObject objectEntryJSONObject = jsonObject.getJSONObject(
			"objectEntry");

		if (objectEntryJSONObject == null) {
			throw new IllegalArgumentException("Object entry is null");
		}

		return objectEntryJSONObject;
	}

	private Map<String, JSONObject> _getObjectEntryJSONObjects(
		String objectDefinitionExternalReferenceCode) {

		return _objectEntryJSONObjectsMap.computeIfAbsent(
			objectDefinitionExternalReferenceCode, key -> new HashMap<>());
	}

	private static final Log _log = LogFactory.getLog(MockObjectEntryManagerRestController.class);

	private static final Map<String, Map<String, JSONObject>>
		_objectEntryJSONObjectsMap = new HashMap<>();

}