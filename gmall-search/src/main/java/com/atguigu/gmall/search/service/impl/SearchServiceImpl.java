package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchParamVo;
import com.atguigu.gmall.search.pojo.SearchResponseAttrValueVo;
import com.atguigu.gmall.search.pojo.SearchResponseVo;
import com.atguigu.gmall.search.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        try {
            SearchResponse response = restHighLevelClient.search(new SearchRequest(new String[]{"goods"}, buildDsl(searchParamVo)), RequestOptions.DEFAULT);

            SearchResponseVo searchResponseVo = parseResult(response);

            searchResponseVo.setPageNum(searchParamVo.getPageNum());
            searchResponseVo.setPageSize(searchParamVo.getPageSize());

            return searchResponseVo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SearchResponseVo parseResult(SearchResponse response) {
        SearchResponseVo responseVo = new SearchResponseVo();
        SearchHits hits = response.getHits();
        //命中结果数量
        responseVo.setTotal(hits.getTotalHits());
        SearchHit[] searchHits = hits.getHits();
        if (searchHits!=null && searchHits.length>0){
            //将结果_source对象转成goods对象
            responseVo.setGoodsList(Arrays.stream(searchHits).map(SearchHit ->{
                //获取_source的json对象
                String json = SearchHit.getSourceAsString();
                Goods goods = JSON.parseObject(json, Goods.class);
                // 如果有高亮 将标题替换成高亮标题
                Map<String, HighlightField> highlightFields = SearchHit.getHighlightFields();
                if (!CollectionUtils.isEmpty(highlightFields)){
                    HighlightField title = highlightFields.get("title");
                    if (title!=null){
                        Text[] fragments = title.getFragments();
                        if (fragments!=null && fragments.length>0){
                            goods.setTitle(fragments[0].string());
                        }
                    }
                }
                return goods;
            }).collect(Collectors.toList()));

        }

        //解析品牌聚合结果集
        Aggregations aggregations = response.getAggregations();
        ParsedLongTerms brandIdAgg = aggregations.get("brandIdAgg");
        List<? extends Terms.Bucket> buckets = brandIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(buckets)){
            responseVo.setBrands(buckets.stream().map(bucket ->{
                BrandEntity brandEntity = new BrandEntity();
                brandEntity.setId(((Terms.Bucket) bucket).getKeyAsNumber().longValue());
                Aggregations aggBrandName = ((Terms.Bucket) bucket).getAggregations();
                ParsedStringTerms brandNameAgg = aggBrandName.get("brandNameAgg");
                List<? extends Terms.Bucket> bucketsBrandName = brandNameAgg.getBuckets();
                if (!CollectionUtils.isEmpty(bucketsBrandName)){
                    brandEntity.setName(bucketsBrandName.get(0).getKeyAsString());
                }
                ParsedStringTerms logoAgg = aggBrandName.get("logoAgg");
                List<? extends Terms.Bucket> logoAggBuckets = logoAgg.getBuckets();
                if (!CollectionUtils.isEmpty(logoAggBuckets)){
                    brandEntity.setLogo(logoAggBuckets.get(0).getKeyAsString());
                }
                return brandEntity;
            }).collect(Collectors.toList()));

        }

        // 规格参数聚合结果集
        ParsedNested attrAgg = aggregations.get("attrAgg");
        ParsedLongTerms attrId = attrAgg.getAggregations().get("attrId");
        List<? extends Terms.Bucket> attrIdBuckets = attrId.getBuckets();
        if (!CollectionUtils.isEmpty(attrIdBuckets)){
            responseVo.setFilters(attrIdBuckets.stream().map(bucket -> {
                SearchResponseAttrValueVo attrValueVo = new SearchResponseAttrValueVo();
                attrValueVo.setAttrId(((Terms.Bucket) bucket).getKeyAsNumber().longValue());
                Aggregations attr = ((Terms.Bucket) bucket).getAggregations();
                ParsedStringTerms attrNameAdd = attr.get("attrNameAdd");
                List<? extends Terms.Bucket> attrNameBuckets = attrNameAdd.getBuckets();
                if (!CollectionUtils.isEmpty(attrNameBuckets)){
                    attrValueVo.setAttrName(attrNameBuckets.get(0).getKeyAsString());
                }
                ParsedStringTerms attrValueAdd = attr.get("attrValueAdd");
                List<? extends Terms.Bucket> attrValueBuckets = attrValueAdd.getBuckets();
                if (!CollectionUtils.isEmpty(attrValueBuckets)){
                    attrValueVo.setAttrValues(attrValueBuckets.stream().map(attrValueBucket ->((Terms.Bucket) attrValueBucket).getKeyAsString()).collect(Collectors.toList()));
                }
                return attrValueVo;
            }).collect(Collectors.toList()));
        }

        // 解析分类结果集
        ParsedLongTerms categoryIdAgg = aggregations.get("categoryIdAgg");
        List<? extends Terms.Bucket> categoryIdAggBuckets = categoryIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(categoryIdAggBuckets)){
            responseVo.setCategories(categoryIdAggBuckets.stream().map(categoryIdBuckets ->{
                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setId( ((Terms.Bucket) categoryIdBuckets).getKeyAsNumber().longValue());
                ParsedStringTerms categoryNameAgg = ((Terms.Bucket) categoryIdBuckets).getAggregations().get("categoryNameAgg");
                List<? extends Terms.Bucket> categoryNameAggBuckets = categoryNameAgg.getBuckets();
                if (!CollectionUtils.isEmpty(categoryNameAggBuckets)){
                    categoryEntity.setName(categoryNameAggBuckets.get(0).getKeyAsString());
                }
                return categoryEntity;
            }).collect(Collectors.toList()));
        }
        return responseVo;
    }

    private SearchSourceBuilder buildDsl(SearchParamVo searchParamVo) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        sourceBuilder.query(boolQueryBuilder);

        //标题匹配查询
        boolQueryBuilder.must(QueryBuilders.matchQuery("title",searchParamVo.getKeyword()).operator(Operator.AND));

        //品牌过滤
        List<Long> brandId = searchParamVo.getBrandId();
        if (!CollectionUtils.isEmpty(brandId)){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",brandId));
        }

        //价格过滤
        Double priceTo = searchParamVo.getPriceTo();
        Double priceFrom = searchParamVo.getPriceFrom();
        // 如果任何一个价格不为空，就需要添加价格区间查询
        if (priceFrom != null || priceTo != null){
            // 构建一个价格区间查询，放入bool查询的过滤条件中
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
            boolQueryBuilder.filter(rangeQuery);
            // 如果起始价格不为空，要添加gte条件
            if (priceFrom != null) {
                rangeQuery.gte(priceFrom);
            }
            // 如果截止价格不为空，要添加lte条件
            if (priceTo != null) {
                rangeQuery.lte(priceTo);
            }
        }


        //分类过滤
        List<Long> categoryId = searchParamVo.getCategoryId();
        if (!CollectionUtils.isEmpty(categoryId)){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId",categoryId));
        }

        //有货无货过滤
        Boolean store = searchParamVo.getStore();
        if (store!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("store",store));
        }

        // 规格参数的过滤条件: ["3:8G-12G", "4:128G-256G"]
        List<String> props = searchParamVo.getProps();
        if (!CollectionUtils.isEmpty(props)){
            props.forEach(porp -> {
                String[] attrs = StringUtils.split(porp, ":");
                if (attrs != null && attrs.length == 2 && NumberUtils.isCreatable(attrs[0])){
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    boolQuery.must(QueryBuilders.termQuery("searchAttrs.attrId",attrs[0]));
                    String[] attrValue = StringUtils.split(attrs[1],"-");
                    boolQuery.must(QueryBuilders.termsQuery("searchAttrs.attrValue",attrValue));
                    boolQueryBuilder.filter(QueryBuilders.nestedQuery("searchAttrs",boolQuery, ScoreMode.None));
                }
            });
        }

        // 排序条件：0-综合排序 1-价格降序 2-价格升序 3-销量降序 4-新品降序
        //sourceBuilder.sort("price", SortOrder.DESC);
        Integer sort = searchParamVo.getSort();
        switch (sort){
            case 0 : sourceBuilder.sort("store",SortOrder.DESC);break;
            case 1 : sourceBuilder.sort("price",SortOrder.DESC);break;
            case 2 : sourceBuilder.sort("price",SortOrder.ASC);break;
            case 3 : sourceBuilder.sort("sales",SortOrder.DESC);break;
            case 4 : sourceBuilder.sort("createTime",SortOrder.DESC);break;
        }

        Integer pageNum = searchParamVo.getPageNum();
        Integer pageSize = searchParamVo.getPageSize();
        if (pageNum!=null && pageSize!=null){
            sourceBuilder.from((pageNum-1)*pageSize);
            sourceBuilder.size(pageSize);
        }

        //高亮
        sourceBuilder.highlighter(new HighlightBuilder()
                .field("title")
                .preTags("<font style='color:red;'>")
                .postTags("</font>"));

        //品牌聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                .subAggregation(AggregationBuilders.terms("logoAgg").field("logo")));

        //分类聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAgg").field("categoryId")
                .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName")));

        //嵌套属性聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("attrAgg","searchAttrs")
                .subAggregation(AggregationBuilders.terms("attrId").field("searchAttrs.attrId")
                .subAggregation(AggregationBuilders.terms("attrNameAdd").field("searchAttrs.attrName"))
                .subAggregation(AggregationBuilders.terms("attrValueAdd").field("searchAttrs.attrValue"))));

        System.out.println(sourceBuilder);

        return sourceBuilder;
    }


    public static void main(String[] args) {
        String porp = "3:8G-12G";
        String[] attrs = StringUtils.split(porp, ":");
        for (String attr : attrs) {
            System.out.println(attr);
        }
    }
}
