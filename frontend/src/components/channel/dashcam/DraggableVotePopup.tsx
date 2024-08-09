import React, { useState, useRef, useCallback } from 'react';
import styled from 'styled-components';
import { CreateOption } from '@/types/channelType';

interface DraggableVotePopupProps {
   title: string;
   content: React.ReactNode;
   onClose: () => void;
   onOptionsChange: (options: CreateOption[]) => void;
}

const DraggableVotePopup: React.FC<DraggableVotePopupProps> = ({ title, content, onClose, onOptionsChange }) => {
   const [isDragging, setIsDragging] = useState(false);
   const [position, setPosition] = useState({ x: 0, y: 0 });
   const popupRef = useRef<HTMLDivElement>(null);

   const handleMouseDown = useCallback((e: React.MouseEvent) => {
      setIsDragging(true);
      popupRef.current!.style.cursor = 'grabbing';
   }, []);

   const handleMouseMove = useCallback((e: React.MouseEvent) => {
      if (isDragging) {
         setPosition((prevPosition) => ({
            x: prevPosition.x + e.movementX,
            y: prevPosition.y + e.movementY,
         }));
      }
   }, [isDragging]);

   const handleMouseUp = useCallback(() => {
      setIsDragging(false);
      popupRef.current!.style.cursor = 'grab';
   }, []);

   return (
      <Overlay>
         <Popup
            ref={popupRef}
            style={{ transform: `translate(${position.x}px, ${position.y}px)` }}
            onMouseDown={handleMouseDown}
            onMouseMove={handleMouseMove}
            onMouseUp={handleMouseUp}
            onMouseLeave={handleMouseUp}
         >
            <Header>
               <Title>{title}</Title>
               <CloseButton onClick={onClose}>x</CloseButton>
            </Header>
            <Content>{content}</Content>
            <VoteForm onOptionsChange={onOptionsChange} onClose={onClose} />
         </Popup>
      </Overlay>
   );
};

const VoteForm: React.FC<{ onOptionsChange: (options: CreateOption[]) => void, onClose: () => void }> = ({ onOptionsChange, onClose }) => {
   const [voteTitle, setVoteTitle] = useState('');
   const [options, setOptions] = useState<CreateOption[]>([{ optionOrder: 1, content: '' }, { optionOrder: 2, content: '' }]);

   const handleVoteTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      setVoteTitle(e.target.value);
   };

   const handleOptionChange = (index: number, value: string) => {
      const newOptions = [...options];
      newOptions[index].content = value;
      setOptions(newOptions);
      onOptionsChange(newOptions);
   };

   const addOption = () => {
      if (options.length < 3) {
         setOptions([...options, { optionOrder: options.length + 1, content: '' }]);
      }
   };

   const removeOption = (index: number) => {
      if (options.length > 2) {
         const newOptions = options.filter((_, i) => i !== index).map((option, idx) => ({ ...option, optionOrder: idx + 1 }));
         setOptions(newOptions);
         onOptionsChange(newOptions);
      }
   };

   const handleCreateVote = () => {
      if (voteTitle.trim() && options.every(option => option.content.trim())) {
         console.log('Vote created:', { voteTitle, options });
         onClose();
      } else {
         alert('투표 제목과 모든 옵션을 입력해주세요.');
      }
   };

   return (
      <VoteContainer>
         <OptionContainer>
            <VoteTitleInput
               placeholder="투표 제목을 입력하세요."
               value={voteTitle}
               onChange={handleVoteTitleChange}
            />
         </OptionContainer>
         {options.map((option, index) => (
            <OptionContainer key={index}>
               <OptionInput
                  placeholder={`옵션 ${option.optionOrder}`}
                  value={option.content}
                  onChange={(e) => handleOptionChange(index, e.target.value)}
               />
               {options.length > 2 && (
                  <RemoveOptionButton onClick={() => removeOption(index)}>x</RemoveOptionButton>
               )}
            </OptionContainer>
         ))}
         {options.length < 3 && (
            <AddOptionButton onClick={addOption}>옵션 추가</AddOptionButton>
         )}
         <CreateVoteButton type="button" onClick={handleCreateVote}>투표 생성</CreateVoteButton>
      </VoteContainer>
   );
};

const Overlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
`;

const Popup = styled.div`
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  cursor: grab;
  max-width: 500px; /* 팝업의 최대 너비 설정 */
  width: 100%;
  box-sizing: border-box; /* 추가된 부분 */
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-right: 20px; /* 추가된 부분 */
`;

const Title = styled.h2`
  margin: 0;
  font-size: 18px;
  padding-right: 20px; /* 추가된 부분 */
`;

const CloseButton = styled.button`
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
`;

const Content = styled.div`
  font-size: 16px;
`;

const VoteContainer = styled.div`
  margin-top: 20px;
`;

const VoteTitleInput = styled.input`
  width: 100%;
  padding: 10px;
  margin-bottom: 20px;
  border: 2px solid #bbb;
  border-radius: 5px;
`;

const OptionContainer = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 10px;
`;

const OptionInput = styled.input`
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
`;

const RemoveOptionButton = styled.button`
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  margin-left: 10px;
`;

const AddOptionButton = styled.button`
  width: 100%;
  padding: 10px;
  border: 1px dashed #ddd;
  border-radius: 5px;
  background: none;
  cursor: pointer;
`;

const CreateVoteButton = styled.button`
  width: 100%;
  padding: 12px;
  background-color: #ffa600;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 10px;

  &:hover {
    background-color: #FF900D;
  }
`;

export default React.memo(DraggableVotePopup);
