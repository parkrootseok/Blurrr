"use client";

import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import BoastCard from '@/components/channel/boast/BoastCard';
import PostTitle from '@/components/channel/PostTitle';
import dummy from "@/db/data.json";
import { fetchPosts } from '@/api/channel';
import { PostData } from '@/types/channelType';

const CardGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
`;

const Boast: React.FC = () => {
   const [Posts, setPosts] = useState<PostData[]>([]);
   const [keyword, setKeyword] = useState('');
   const [sortCriteria, setSortCriteria] = useState('TIME');

   const handleSortChange = (newSort: string) => {
      // 정렬 기준을 변경하고, API에서 사용할 수 있는 형식으로 변환
      const criteriaMap: { [key: string]: string } = {
         '최신순': 'TIME',
         '댓글수': 'COMMENT',
         '조회수': 'VIEW',
         '좋아요': 'LIKE'
      };

      const newCriteria = criteriaMap[newSort] || 'TIME'; // 매핑되지 않는 경우 기본값 설정
      setSortCriteria(newCriteria);
   };

   const handleSearch = (newKeyword: string) => {
      setKeyword(newKeyword);
   };

   useEffect(() => {
      const loadData = async () => {
         try {
            const data = await fetchPosts('11ef4e3b-36d9-9292-a7f5-1d7ed3babb22', keyword, 0, sortCriteria);
            setPosts(data);
            console.log('Posts loaded:', data);
         } catch (error) {
            console.error('Failed to load channel board list data:', error);
         }
      };

      loadData();
   }, [keyword, criteria]);

   return (
      <>
         <PostTitle
            channel="boast"
            title="내 차를 자랑해보자"
            onSearch={handleSearch}
            onSortChange={handleSortChange}
         />
         <CardGrid>
            {Posts.map((post) => (
               <div key={post.board.id} onClick={() => console.log("item pressed")}>
                  <BoastCard
                     thumbnail={post.board.}
                     viewer={item.viewer}
                     likes={item.likes}
                  />
               </div>
            ))}
         </CardGrid>
      </>
   );
};

export default Boast;
