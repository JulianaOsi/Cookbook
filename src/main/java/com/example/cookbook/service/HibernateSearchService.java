package com.example.cookbook.service;

import com.example.cookbook.domain.Recipe;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@Service
public class HibernateSearchService {
    @Autowired
    private final EntityManager entityManager;

    private QueryBuilder queryBuilder;
    private FullTextEntityManager fullTextEntityManager;

    @Autowired
    public HibernateSearchService(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
        initializeHibernateSearch();
        createQueryBuilder();
    }

    public void initializeHibernateSearch() {
        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createQueryBuilder() {
        fullTextEntityManager
                = Search.getFullTextEntityManager(entityManager);

        queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Recipe.class)
                .get();
    }

    public List<Recipe> search(String text) {
        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(2)
                .withPrefixLength(0)
                .onField("title")
                .matching(text)
                .createQuery();
        FullTextQuery jpaQuery
                = fullTextEntityManager.createFullTextQuery(query, Recipe.class);

        return jpaQuery.getResultList();
    }
}
