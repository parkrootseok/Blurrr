"use client";

import React, { useState, useEffect } from "react";
import styled, { keyframes } from "styled-components";
import { FaRegHeart, FaHeart } from "react-icons/fa";
import { FaRegTrashAlt } from "react-icons/fa";
import Breadcrumb from "@/components/common/UI/BreadCrumb";
import LeagueDetailTitle from "@/components/league/detail/LeagueDetailTitle";

import { BoardDetail, LeagueList, UserLeague } from "@/types/leagueTypes";
import { fetchLeagueDetail, fetchBoardDelete } from "@/api/league";

import { fetchComment } from "@/types/commentTypes";

import { useRouter } from "next/navigation";
import { useLeagueStore } from "@/store/leagueStore";
import { useAuthStore } from "@/store/authStore";
import { fetchLeagueCommentList } from "@/api/comment";
import { fetchUserLeagueList } from "@/api/league";
import CommentList from "@/components/common/UI/comment/CommentList";
import { fetchLeagueLike, fetchLeagueLikeDelete } from "@/api/board";
import { formatPostDate } from "@/utils/formatPostDate";
import Loading from "@/components/common/UI/Loading";
import NoCarPopup from "@/components/league/NoCarPopup";
import NoAuthority from "@/components/league/NoAuthority";
import LoginForm from "@/components/login/LoginForm";

export default function BoardDetailPage({
  params,
}: {
  params: { leagueName: string; boardId: string };
}) {
  const router = useRouter();

  const encodedLeagueName = params.leagueName;
  const leagueName = decodeURIComponent(encodedLeagueName);
  const boardId = params.boardId;

  const { activeTab, setActiveTab, userLeagueList } = useLeagueStore();
  const { isLoggedIn, user } = useAuthStore();

  const [boardDetail, setBoardDetail] = useState<BoardDetail | null>(null);
  const [commentList, setCommentList] = useState<fetchComment | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [isLiked, setIsLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);

  const [showPopup, setShowPopup] = useState(false);
  const [showLogin, setShowLogin] = useState(false);
  const [showNoAuthority, setShowNoAuthority] = useState(false);

  const loadBoardDetail = async () => {
    try {
      const details = await fetchLeagueDetail(boardId);
      setBoardDetail(details);
      setIsLiked(details.isLike);
      setLikeCount(details.likeCount);
    } catch (error) {
      console.log(error);
    }
  };

  const loadCommentDetail = async (leagueId: string) => {
    try {
      const fetchcommentsList = await fetchLeagueCommentList(leagueId, boardId);
      setCommentList(fetchcommentsList);
    } catch (error) {
      console.log(error);
    }
  };

  const toggleLike = async () => {
    if (isLiked) {
      const likeData = await fetchLeagueLikeDelete(boardId);
      setLikeCount(likeData.likeCount);
      setIsLiked(likeData.isLike);
    } else {
      const likeData = await fetchLeagueLike(boardId);
      setLikeCount(likeData.likeCount);
      setIsLiked(likeData.isLike);
    }
  };

  const checkLeagueInUserList = () => {
    return userLeagueList.some((league) => league.name === leagueName);
  };

  useEffect(() => {
    if (!isLoggedIn) {
      setShowLogin(true);
      setLoading(false);
    } else if (!user?.isAuth) {
      setShowPopup(true);
      setLoading(false);
      return;
    } else if (!checkLeagueInUserList()) {
      setShowNoAuthority(true);
      setLoading(false);
    }
    loadBoardDetail();
  }, [boardId, isLoggedIn, user]);

  useEffect(() => {
    const loadBoardData = async () => {
      const findActiveTab = userLeagueList.find((t) => t.name === leagueName);

      if (findActiveTab) {
        setActiveTab(findActiveTab);
        await loadCommentDetail(findActiveTab.id);
        setLoading(false);
      } else {
        if (!user?.isAuth) {
          setShowPopup(true);
        } else {
          setShowNoAuthority(true);
        }
        setLoading(false);
      }
    };

    if (isLoggedIn && user?.isAuth) {
      if (activeTab.id) {
        loadCommentDetail(activeTab.id);
        setLoading(false);
      } else {
        loadBoardData();
      }
    }
  }, [activeTab, boardId, leagueName, setActiveTab, isLoggedIn, user]);

  const handleCommentAdded = async () => {
    if (activeTab && activeTab.id) {
      await loadCommentDetail(activeTab.id);
    }
  };

  const closePopup = () => {
    setShowPopup(false);
    setShowLogin(false);
    setShowNoAuthority(false);
    router.back();
  };
  const closeLoginPopup = () => {
    router.back();
  };

  if (loading || !boardDetail || !commentList) {
    if (showLogin) {
      return (
        <ModalOverlay onClick={closePopup}>
          <ModalContent onClick={(e) => e.stopPropagation()}>
            <LoginForm closeLoginModal={closePopup} />
            <CloseIcon onClick={closePopup}>×</CloseIcon>
          </ModalContent>
        </ModalOverlay>
      );
    }
    if (showPopup) {
      return <NoCarPopup closePopup={closePopup} />;
    }
    if (showNoAuthority) {
      return <NoAuthority closePopup={closePopup} />;
    }
    return <Loading />;
  }

  return (
    <>
      <BreadcrumbContainer>
        <Breadcrumb
          channel="리그"
          subChannel={leagueName}
          channelUrl={`/league/${leagueName}`}
        />
      </BreadcrumbContainer>
      <LeagueDetailTitle
        title={boardDetail.title}
        createdAt={formatPostDate(boardDetail.createdAt)}
        viewCount={boardDetail.viewCount}
        likeCount={likeCount}
        username={boardDetail.member.nickname}
        authorprofileUrl={boardDetail.member.profileUrl}
        authorCarTitle={boardDetail.member.carTitle}
        boardId={boardId}
        leagueName={leagueName}
      />
      <Content dangerouslySetInnerHTML={{ __html: boardDetail.content }} />
      <CommentContainer>
        <WriterContainer>
          <HeartButton onClick={toggleLike} $isLiked={isLiked}>
            {isLiked ? <FaHeart /> : <FaRegHeart />}
          </HeartButton>
        </WriterContainer>
        <CommentList
          comments={commentList.comments}
          commentCount={commentList.commentCount}
          boardId={boardId}
          leagueId={activeTab.id}
          isLeague={true}
          onCommentAdded={handleCommentAdded}
          boardAuthor={boardDetail.member.nickname}
        />
      </CommentContainer>
      {showPopup && <NoCarPopup closePopup={closePopup} />}
      {showLogin && (
        <ModalOverlay onClick={closePopup}>
          <ModalContent onClick={(e) => e.stopPropagation()}>
            <LoginForm closeLoginModal={closePopup} />
            <CloseIcon onClick={closePopup}>×</CloseIcon>
          </ModalContent>
        </ModalOverlay>
      )}
      {showNoAuthority && <NoAuthority closePopup={closePopup} />}
    </>
  );
}

const BreadcrumbContainer = styled.div`
  width: 100%;
  margin-bottom: 16px;
`;

const Content = styled.div`
  font-size: 16px;
  line-height: 1.5;
  color: #333;
  padding: 16px 16px 40px 16px;
  border-top: 1px solid #bebebe;

  @media (min-width: 480px) {
    font-size: 17px;
    padding: 20px 20px 50px 20px;
  }
`;

const CommentContainer = styled.div`
  display: flex;
  flex-direction: column;
  border-top: 1px solid #bebebe;
`;

const WriterContainer = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
`;

const WriterButton = styled.p`
  padding: 0px;
  font-size: 17px;
  background-color: white;
  margin: 5px 10px 20px 0;
  cursor: pointer;

  &:hover {
    color: #666;
  }
`;

const HeartButton = styled.button<{ $isLiked: boolean }>`
  margin: 5px 0px 2px 0px;
  padding: 0;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: ${({ $isLiked }) => ($isLiked ? "#d60606" : "#333")};
  display: flex;
  justify-content: center;
  align-items: center;

  &:hover {
    color: ${({ $isLiked }) => ($isLiked ? "#ff6666" : "#d60606")};
  }

  @media (min-width: 768px) {
    font-size: 20px;
  }
`;

const fadeIn = keyframes`
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
`;

const fadeOut = keyframes`
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(-20px);
  }
`;

const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

const ModalContent = styled.div`
  background: #ffffff;
  padding: 2em;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: relative;
  max-width: 50%;
  width: 100%;
  animation: ${fadeIn} 300ms ease-in-out;

  &.fade-out {
    animation: ${fadeOut} 300ms ease-in-out;
  }
`;

const CloseIcon = styled.span`
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
  color: #999;
  &:hover {
    color: #333;
  }
`;
