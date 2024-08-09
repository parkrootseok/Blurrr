"use client";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

import styled from "styled-components";
import HotArticleList from "@/components/main/HotArticleList";
import BlackboxList from "@/components/main/BlackboxList";
import CarPictureList from "@/components/main/CarPictureList";

import LeagueList from "@/components/main/LeagueList";
import TopCarCard from "@/components/main/aside/TopCarCard";

import { LuTrophy } from "react-icons/lu";
import { IoArrowForward } from "react-icons/io5";
import { FaCarTunnel } from "react-icons/fa6";
import { BsFire } from "react-icons/bs";
import { FaCrown } from "react-icons/fa6";
import { PiRanking } from "react-icons/pi";

import LeagueRanking from "@/components/main/aside/LeageRanking";
import UserCarInfo from "@/components/main/user/UserCarInfo";
import FollowChannelInfo from "@/components/main/user/FollowChannelInfo";

import { useLeagueStore } from "@/store/leagueStore";
import { useAuthStore } from "@/store/authStore";
import { fetchBrandLeagues, fetchUserLeagueList } from "@/api/league";
import {
  fetchHotArticles,
  fetchLeagueRanking,
  fetchMyCars,
  fetchTodayCar,
} from "@/api/mainPage";
import { fetchFollowingChannels } from "@/api/channel";
import { Channels } from "@/types/channelType";
import { UserLeague, LeagueList as LeagueListType } from "@/types/leagueTypes";
import { HotBoardItem, TodayCarItem } from "@/types/mainPageTypes";
import Loading from "@/components/common/UI/Loading";

export default function Home() {
  const router = useRouter();
  const {
    userLeagueList,
    setUserLeagueList,
    brandLeagueList,
    setBrandLeagueTab,
    initialized,
    setInitialized,
  } = useLeagueStore();
  const { isLoggedIn, user } = useAuthStore();

  const [hotBoards, setHotBoards] = useState<HotBoardItem[]>([]);
  const [todayCar, setTodayCar] = useState<TodayCarItem | null>(null);
  const [myCarBoards, setMyCarBoards] = useState<TodayCarItem[]>([]);
  const [followChannels, setFollowChannels] = useState<Channels[]>([]);
  const [leaugeRanking, setLeagueRanking] = useState<LeagueListType[]>([]);

  useEffect(() => {
    const fetchMainProps = async () => {
      try {
        const hot = await fetchHotArticles();
        setHotBoards(hot);

        // const today = await fetchTodayCar();
        // setTodayCar(today);

        // const car = await fetchMyCars();
        // setMyCarBoards(car);

        const ranking = await fetchLeagueRanking();
        setLeagueRanking(ranking);

        if (isLoggedIn) {
          const follow = await fetchFollowingChannels();
          setFollowChannels(follow);
        }
      } catch (error) {
        console.log(error);
      }
    };

    fetchMainProps();
  }, [isLoggedIn]);

  useEffect(() => {
    const initailizeTabs = async () => {
      if (!initialized) {
        try {
          const leagues = await fetchBrandLeagues();
          setBrandLeagueTab(leagues);
          if (isLoggedIn) {
            if (user?.isAuth) {
              const userLeagues: UserLeague[] = await fetchUserLeagueList();
              const userTabs: LeagueListType[] = userLeagues.map(
                (userLeague) => ({
                  id: userLeague.league.id,
                  name: userLeague.league.name,
                  type: userLeague.league.type,
                  peopleCount: userLeague.league.peopleCount,
                })
              );
              setUserLeagueList(userTabs);
            } else {
              const userTabs: LeagueListType[] = [];
              setUserLeagueList(userTabs);
            }
          }
          setInitialized(true);
        } catch (error) {
          console.log(error);
        }
      }
    };
    initailizeTabs();
  }, [
    isLoggedIn,
    initialized,
    setBrandLeagueTab,
    setInitialized,
    user,
    setUserLeagueList,
  ]);

  const handleMoreClickLeage = () => {
    router.push("/league");
  };
  const handleMoreClickDashcam = () => {
    router.push("/channels/dashcam");
  };

  const handleMoreClickBoast = () => {
    router.push("/channels/boast");
  };

  const handleCarCertification = () => {
    router.push("/carcertification");
  };

  if (!hotBoards.length) {
    return <Loading />;
  }

  return (
    <PageContainer>
      <GridContainer>
        <Main>
          {isLoggedIn && (
            <UserInfoContainer>
              {userLeagueList.length > 0 ? (
                <UserCarInfo userLeagueList={userLeagueList} />
              ) : (
                <NoAuth>
                  <NoAuthTitle>
                    등록된 차가 없습니다. <br /> 차를 등록해주세요
                  </NoAuthTitle>
                  <CarCerficationButton onClick={handleCarCertification}>
                    <FaCarTunnel />
                    등록하러 가기
                  </CarCerficationButton>
                </NoAuth>
              )}
              <FollowChannelInfo followChannels={followChannels} />
            </UserInfoContainer>
          )}
          <ArticleSection>
            <SectionTitle>
              <BsFire />
              Hot
            </SectionTitle>
            <HotArticleList hotBoards={hotBoards} />
          </ArticleSection>
          <ArticleSection>
            <SectionHeader>
              <SectionTitle>브랜드 리그</SectionTitle>
              <MoreButton onClick={handleMoreClickLeage}>
                더보기
                <IoArrowForward />
              </MoreButton>
            </SectionHeader>
            <LeagueList />
          </ArticleSection>
          <ArticleSection>
            <SectionHeader>
              <SectionTitle>블랙박스</SectionTitle>
              <MoreButton onClick={handleMoreClickDashcam}>
                더보기
                <IoArrowForward />
              </MoreButton>
            </SectionHeader>
            <BlackboxList />
          </ArticleSection>
          <ArticleSection>
            <SectionHeader>
              <SectionTitle>차 자랑</SectionTitle>
              <MoreButton onClick={handleMoreClickBoast}>
                더보기
                <IoArrowForward />
              </MoreButton>
            </SectionHeader>
            <CarPictureList />
          </ArticleSection>
        </Main>
        <Aside>
          <AsideSection>
            <AsideSectionTitle className="today">
              <FaCrown />
              오늘의 차
            </AsideSectionTitle>
            <TopCarCard />
          </AsideSection>
          <AsideSection>
            <AsideSectionTitle>
              <PiRanking />
              주간 리그 순위
            </AsideSectionTitle>
            <LeagueRanking leaugeRanking={leaugeRanking} />
          </AsideSection>
        </Aside>
      </GridContainer>
    </PageContainer>
  );
}

const PageContainer = styled.div`
  padding-top: 20px;
`;

const GridContainer = styled.div`
  display: flex;

  @media (min-width: 1024px) {
    flex-direction: row;
    border-top: none;
    width: 100%;
  }
`;

const Main = styled.main`
  /* display: block; */
  padding: 10px;
  width: 100%;
  flex-shrink: 1; /* 크기가 줄어들 수 있음 */

  @media (min-width: 1024px) {
    /* width: 736px; */

    width: 70%;
  }
`;

const Aside = styled.aside`
  display: none;

  @media (min-width: 1024px) {
    display: block;
    width: 300px;
    margin-left: 64px;
    /* flex-shrink: 0; */
  }
`;

const SectionTitle = styled.h1`
  font-size: 1.3rem;
  font-weight: bold;
  margin-top: 0px;
  margin-bottom: 6px;
  display: flex;
  align-items: center;

  svg {
    margin-right: 5px;
  }
`;

const ArticleSection = styled.div`
  margin: 0 10px 50px;

  @media (min-width: 768px) {
    margin: 0 20px 50px;
  }
`;

const SectionHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const AsideSection = styled.div`
  padding: 16px 24px;
  margin: 20px 0px;

  /* background-color: #f9f9f9; */
  background-color: #f8f8f8;
  /* border: 2px solid #e5e7eb; */
  border-radius: 8px;
  /* box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); */
  align-items: center;
`;

const AsideSectionTitle = styled.h2`
  font-size: 16px;
  margin-top: 2px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;

  &.today {
    svg{
      color: #fbc02d;
    }
  }

  svg {
    margin-right: 5px;
    /* color: #fbc02d; */
  }
`;

const UserInfoContainer = styled.div`
  margin: 40px 0;
  display: flex;
  justify-content: center;
`;

const MoreButton = styled.button`
  background: none;
  border: none;
  color: gray;
  font-size: 12px;
  cursor: pointer;
  display: felx;
  align-items: center;

  svg {
    margin-left: 4px;
  }

  @media (min-width: 1024px) {
    font-size: 13px;
  }
`;

const NoAuth = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 28px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  background: #ffffff;
  margin-left: 10px;

  @media (min-width: 480px) {
    width: 70%;
  }

  @media (min-width: 768px) {
    margin-right: 30px;
    width: 48%;
  }

  @media (min-width: 1024px) {
    width: 50%;
  }

  @media (min-width: 1440px) {
    width: 70%;
    padding: 28px 20px;
  }
`;

const NoAuthTitle = styled.h3`
  width: 70%;
  font-size: 16px;
  text-align: center;
  @media (min-width: 768px) {
    font-size: 20px;
  }
`;

const CarCerficationButton = styled.button`
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  padding: 20px;
  margin: 10px 10px;
  width: 60%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: 16px;

  &:hover {
    background: #f5f5f5;
  }

  svg {
    margin-right: 5px;
    /* color: ${({ theme }) => theme.colors.main}; */
  }
`;
