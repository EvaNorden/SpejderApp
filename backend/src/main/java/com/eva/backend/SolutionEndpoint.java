package com.eva.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "solutionApi",
        version = "v1",
        resource = "solution",
        namespace = @ApiNamespace(
                ownerDomain = "backend.eva.com",
                ownerName = "backend.eva.com",
                packagePath = ""
        )
)
public class SolutionEndpoint {

    private static final Logger logger = Logger.getLogger(SolutionEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Solution.class);
    }

    /**
     * Returns the {@link Solution} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Solution} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "solution/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Solution get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Solution with ID: " + id);
        Solution solution = ofy().load().type(Solution.class).id(id).now();
        if (solution == null) {
            throw new NotFoundException("Could not find Solution with ID: " + id);
        }
        return solution;
    }

    /**
     * Inserts a new {@code Solution}.
     */
    @ApiMethod(
            name = "insert",
            path = "solution",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Solution insert(Solution solution) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that solution.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(solution).now();
        logger.info("Created Solution with ID: " + solution.getId());

        return ofy().load().entity(solution).now();
    }

    /**
     * Updates an existing {@code Solution}.
     *
     * @param id       the ID of the entity to be updated
     * @param solution the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Solution}
     */
    @ApiMethod(
            name = "update",
            path = "solution/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Solution update(@Named("id") Long id, Solution solution) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(solution).now();
        logger.info("Updated Solution: " + solution);
        return ofy().load().entity(solution).now();
    }

    /**
     * Deletes the specified {@code Solution}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Solution}
     */
    @ApiMethod(
            name = "remove",
            path = "solution/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Solution.class).id(id).now();
        logger.info("Deleted Solution with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "solution",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Solution> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Solution> query = ofy().load().type(Solution.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Solution> queryIterator = query.iterator();
        List<Solution> solutionList = new ArrayList<Solution>(limit);
        while (queryIterator.hasNext()) {
            solutionList.add(queryIterator.next());
        }
        return CollectionResponse.<Solution>builder().setItems(solutionList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Solution.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Solution with ID: " + id);
        }
    }
}