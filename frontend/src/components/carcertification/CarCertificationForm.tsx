import React, { useState, useRef, useEffect, ChangeEvent } from "react";
import styled from "styled-components";
import { useRouter } from "next/navigation";
import { MdAddPhotoAlternate } from "react-icons/md";
import axios from "axios";
import { submitImageForOCR } from '@/api/carcertification'

const CarCertificationForm = () => {
  const [imageSrc, setImageSrc] = useState<string | null>(null);
  const [videoStream, setVideoStream] = useState<MediaStream | null>(null);
  const [ocrResults, setOcrResults] = useState<{ vehicle_model: string | null, preprocessed_image: string | null }>({ vehicle_model: null, preprocessed_image: null });
  const [preprocessedImage, setPreprocessedImage] = useState<string | null>(null);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const router = useRouter();

  useEffect(() => {
    return () => {
      if (videoStream) {
        videoStream.getTracks().forEach((track) => track.stop());
      }
    };
  }, [videoStream]);

  const handleCaptureClick = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true });
      setVideoStream(stream);
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        videoRef.current.play();
      }
    } catch (error) {
      console.error("새로고침을 해주세요.", error);
    }
  };

  const handleVideoClick = () => {
    if (videoRef.current) {
      const canvas = document.createElement("canvas");
      canvas.width = videoRef.current.videoWidth;
      canvas.height = videoRef.current.videoHeight;
      canvas.getContext("2d")?.drawImage(videoRef.current, 0, 0);

      setImageSrc(canvas.toDataURL("image/png"));

      videoRef.current.pause();
      videoRef.current.srcObject = null;
      if (videoStream) {
        videoStream.getTracks().forEach((track) => track.stop());
      }
      setVideoStream(null);
    }
  };

  const handleImageUpload = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setImageSrc(e.target?.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async () => {
    if (!imageSrc) return;

    try {
        const result = await submitImageForOCR(imageSrc);
        if (result && result.extracted_texts) {
          setOcrResults({
            vehicle_model: result.vehicleModel || null, 
            preprocessed_image: result.preprocessed_image || null
          });


        } else {
          console.error("OCR 결과가 없습니다:", result);
        }
      } catch (error) {
        console.error("Error submitting image:", error);
      }
};

  return (
    <Container>
      <Head>
        <Heading>자동차 등록증 업로드</Heading>
        <SubHeading>내 차 인증을 하고 다양한 서비스를 이용해보세요!</SubHeading>
        <SubSubHeading>
          * 자동차 등록증은 사용자 일치 여부 확인 후 폐기될 예정입니다.
        </SubSubHeading>
      </Head>
      <Body>
        <Button onClick={handleCaptureClick}>자동차 등록증 촬영하기</Button>
        <ImageBox
          onClick={() => document.getElementById("uploadImage")?.click()}
        >
          {videoStream ? (
            <Video ref={videoRef} autoPlay onClick={handleVideoClick} />
          ) : imageSrc ? (
            <Image src={imageSrc} alt="자동차 등록증 이미지 업로드" />
          ) : (
            <Placeholder><MdAddPhotoAlternate/></Placeholder>
            
          )}
        </ImageBox>
      </Body>
      <input
        type="file"
        id="uploadImage"
        accept="image/*"
        style={{ display: "none" }}
        onChange={handleImageUpload}
      />
       <Button onClick={handleSubmit}>제출</Button>
      {ocrResults.vehicle_model && (
        <ResultsContainer>
          <Div>
            차량 모델 : {ocrResults.vehicle_model}
          </Div>
        </ResultsContainer>
      )}
      <ButtonContainer>
        <Button onClick={() => router.push("/")}>다음에 할게요.</Button>
        <Button onClick={() => router.push("/")}>완료</Button>
      </ButtonContainer>
      
    </Container>
  );
};

export default CarCertificationForm;

const Container = styled.div`
  display: flex;
  flex-direction: column;
  text-align: center;
  justify-content: center;
  width: 350px;
  height: 100%;
  margin: 20px;
  padding: 40px;
  background-color: #f0f0f0;
  gap: 20px;
  border-radius: 15px;
`;

const Head = styled.div`
  display: flex;
  flex-direction: column;
  font-weight: bold;
`;


const Body = styled.div`
  display: flex;
  flex-direction: column;
  font-weight: bold;
  gap: 20px;
`;

const Heading = styled.h2`
  margin: 5px 0;
`;

const SubHeading = styled.h4`
  margin: 5px 0;
`;

const SubSubHeading = styled.h6`
  margin: 5px 0;
`;

const Button = styled.button`
  padding: 1.2em;
  font-size: 0.7em;
  color: #fff;
  background-color: #f9803a;
  border: none;
  border-radius: 5px;
  cursor: pointer;

  &:hover {
    background-color: #ff5e01;
  }

  &:disabled {
    background-color: #ddd;
    cursor: not-allowed;
  }
`;

const ImageBox = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 200px;
  border: 2px dashed #ccc;
  cursor: pointer;
`;

const Image = styled.img`
  max-width: 100%;
  max-height: 100%;
`;

const Video = styled.video`
  max-width: 100%;
  max-height: 100%;
`;

const Placeholder = styled.span`
  color:#ccc;
`;

const ButtonContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px;
`;

const ResultsContainer = styled.div`
  display: flex;
  margin-top: 10px;
  text-align: left;
`;

const Div = styled.div`
  display: block;
  font-weight: 400;
  font-size: 1em; 
  color: #000; 
`;